package com.uniandes.modus.model

import com.uniandes.modus.getCacheDataBase
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class UserSaveGarmentRepository private constructor() {

    private val supabase = SupabaseConfig.instance.client
    private val garmentRepository = GarmentRepository.instance
    private val dbCache = getCacheDataBase()!!

    companion object {
        val instance: UserSaveGarmentRepository by lazy { UserSaveGarmentRepository() }
    }

    @Serializable
    data class UserSaveGarment(
        @SerialName("user_document") val userDocument: String,
        @SerialName("garment_reference") val garmentReference: String,
        val date: Instant,
        @SerialName("users") val user: UserRepository.User? = null,
        @SerialName("garments") val garment: GarmentRepository.Garment? = null
    )

    suspend fun saveGarment(userDocument: String, garmentReference: String): Boolean {
        return try {
            val userSaveGarment = UserSaveGarment(
                userDocument = userDocument,
                garmentReference = garmentReference,
                date = Clock.System.now()
            )

            supabase.postgrest["user_save_garment"].insert(userSaveGarment)

            dbCache.userSaveGarmentCacheQueries.insertOne(
                garmentReference,
                Json.encodeToString(garmentRepository.getGarment(garmentReference))
            )

            true
        } catch (e: Exception) {
            e.printStackTrace()

            dbCache.userSaveGarmentQueueQueries.insertQueue(
                userDocument,
                garmentReference,
                "save",
                Clock.System.now().toString()
            )

            false
        }
    }

    suspend fun notSaveGarment(userDocument: String, garmentReference: String): Boolean {
        return try {
            supabase.postgrest["user_save_garment"].delete {
                filter {
                    eq("user_document", userDocument)
                    eq("garment_reference", garmentReference)
                }
            }

            dbCache.userSaveGarmentCacheQueries.deleteOne(garmentReference)

            true
        } catch (e: Exception) {
            e.printStackTrace()

            dbCache.userSaveGarmentQueueQueries.insertQueue(
                userDocument,
                garmentReference,
                "delete",
                Clock.System.now().toString()
            )

            false
        }
    }

    suspend fun syncGarmentQueue() {
        val queueItems = dbCache.userSaveGarmentQueueQueries.selectAllQueue().executeAsList()

        println("QUEUE ITEMS: $queueItems")

        queueItems.forEach { item ->
            try {
                when (item.action) {
                    "save" -> {
                        val userSaveGarment = UserSaveGarment(
                            userDocument = item.user_document,
                            garmentReference = item.garment_reference,
                            date = Clock.System.now()
                        )
                        supabase.postgrest["user_save_garment"].insert(userSaveGarment)
                    }
                    "delete" -> {
                        supabase.postgrest["user_save_garment"].delete {
                            filter {
                                eq("user_document", item.user_document)
                                eq("garment_reference", item.garment_reference)
                            }
                        }
                    }
                }

                dbCache.userSaveGarmentQueueQueries.deleteQueueById(item.id)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getSavedGarmentsByUserDocument(userDocument: String): List<GarmentRepository.Garment> {
        val columns = Columns.raw("""
        user_document, garment_reference, date,
        garments (reference, name, images, sizes, category)
        """.trimIndent())

        return try {

            val garments = supabase.postgrest["user_save_garment"]
                .select(columns = columns) {
                    filter {
                        eq("user_document", userDocument)
                    }
                }
                .decodeList<UserSaveGarment>()
                .sortedByDescending { it.date }
                .mapNotNull { it.garment }

            dbCache.transaction {
                garments.forEach {
                    dbCache.userSaveGarmentCacheQueries.insertOne(
                        it.reference,
                        Json.encodeToString(it)
                    )
                }
            }

            garments
        } catch (e: Exception) {
            e.printStackTrace()
            dbCache.userSaveGarmentCacheQueries
                .selectAll()
                .executeAsList()
                .map {
                    Json.decodeFromString<GarmentRepository.Garment>(it.garment)
                }
        }
    }

}