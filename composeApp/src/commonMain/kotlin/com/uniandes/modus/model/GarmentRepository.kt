package com.uniandes.modus.model

import com.uniandes.modus.getCacheDataBase
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class GarmentRepository private constructor() {

    private val supabase = SupabaseConfig.instance.client
    private val dbCache = getCacheDataBase()!!

    companion object {
        val instance: GarmentRepository by lazy { GarmentRepository() }
    }

    @Serializable
    data class Garment(
        val reference: String,
        val name: String,
        val images: Map<String, List<String>>,
        val sizes: List<String>,
        val category: String
    )

    fun getGarment(reference: String): Garment? {
        return try {
            dbCache.garmentCacheQueries.selectOne(reference).executeAsOneOrNull()?.let {
                Garment(
                    reference = it.reference,
                    name = it.name,
                    images = Json.decodeFromString<Map<String, List<String>>>(it.images),
                    sizes = Json.decodeFromString<List<String>>(it.sizes),
                    category = it.category
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getGarments(): List<Garment> {
        return try {
            dbCache.garmentCacheQueries
                .selectAll()
                .executeAsList()
                .map {
                    Garment(
                        reference = it.reference,
                        name = it.name,
                        images = Json.decodeFromString<Map<String, List<String>>>(it.images),
                        sizes = Json.decodeFromString<List<String>>(it.sizes),
                        category = it.category
                    )
                }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun updateGarmentsFromNetwork() {
        try {
            val garments = supabase.postgrest["garments"].select().decodeList<Garment>()

            dbCache.transaction {
                garments.forEach { garment ->
                    dbCache.garmentCacheQueries.insertOrReplace(
                        reference = garment.reference,
                        name = garment.name,
                        images = Json.encodeToString(garment.images),
                        sizes = Json.encodeToString(garment.sizes),
                        category = garment.category
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}