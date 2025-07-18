package com.uniandes.modus.model

import com.uniandes.modus.getNowInstant
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class ReviewRepository private constructor() {

    private val supabase = SupabaseConfig.instance.client

    companion object {
        val instance: ReviewRepository by lazy { ReviewRepository() }
    }

    @Serializable
    data class Review(
        @SerialName("user_document") val userDocument: String,
        @SerialName("users") val user: UserRepository.User? = null,
        val year: String,
        val week: String,
        val number: String,
        val voucher: String,
        val description: String?,
        @SerialName("date_creation") val dateCreation: Instant,
        @SerialName("date_review") val dateReview: Instant?,
        val state: String,
    )

    suspend fun add(userDocument: String, year: String, week: String, voucherByteArray: ByteArray): Boolean {
        return try {
            val response = supabase.storage["vouchers"].upload(
                path = "voucher_${getNowInstant().toEpochMilliseconds()}.jpg",
                data = voucherByteArray
            )

            val url = supabase.storage.from("vouchers").publicUrl(response.path)

            val nextReviewNumber = ((getLatestReviewOfWeekPaymentByUser(userDocument, year, week)?.number?.toIntOrNull() ?: 0) + 1).toString()

            val review = Review(
                userDocument = userDocument,
                year = year,
                week = week,
                number = nextReviewNumber,
                voucher = url,
                description = null,
                dateCreation = getNowInstant(),
                dateReview = null,
                state = "3"
            )

            supabase.postgrest["reviews"].insert(review)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getLatestReviewOfWeekPaymentByUser(userDocument: String, year: String, week: String): Review? {
        return try {
            supabase.postgrest["reviews"]
                .select {
                    filter {
                        eq(column = "user_document", value = userDocument)
                        eq(column = "year", value = year)
                        eq(column = "week", value = week)
                    }
                }
                .decodeList<Review>()
                .maxByOrNull { it.number.toIntOrNull() ?: 0 }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getReviewsByYear(year: String): List<Review> {
        val columns = Columns.raw("""
        user_document, year, week, number, voucher, description, date_creation, date_review, state,
        users (name, email, phone, department, city, address, role, document)
        """.trimIndent())

        return try {
            supabase.postgrest["reviews"].select(columns = columns) {
                filter {
                    eq(column = "year", value = year)
                }
            }.decodeList<Review>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun approveVoucher(userDocument: String, year: String, week: String, number: String): Boolean {
        return try {
            supabase.postgrest["reviews"].update (
                {
                    set(column = "state", value = "1")
                    set(column = "date_review", value = getNowInstant())
                    set(column = "description", value = "The voucher has been approved by the administrator")
                }
            ) {
                filter {
                    eq(column = "user_document", value = userDocument)
                    eq(column = "year", value = year)
                    eq(column = "week", value = week)
                    eq(column = "number", value = number)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun rejectVoucher(userDocument: String, year: String, week: String, number: String, description: String): Boolean {
        return try {
            supabase.postgrest["reviews"].update (
                {
                    set(column = "state", value = "2")
                    set(column = "date_review", value = getNowInstant())
                    set(column = "description", value = description)
                }
            ) {
                filter {
                    eq(column = "user_document", value = userDocument)
                    eq(column = "year", value = year)
                    eq(column = "week", value = week)
                    eq(column = "number", value = number)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteReview(userDocument: String, year: String, week: String, number: String, voucher: String): Boolean {
        return try {
            supabase.postgrest["reviews"].delete {
                filter {
                    eq("user_document", userDocument)
                    eq("year", year)
                    eq("week", week)
                    eq("number", number)
                }
            }
            //supabase.storage["vouchers"].delete(paths = listOf(voucher.substringAfter("/vouchers/").trimStart('/')))
            //supabase.storage.from(bucketId = "vouchers").delete(paths = listOf("/voucher_1742588405722.jpg"))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}