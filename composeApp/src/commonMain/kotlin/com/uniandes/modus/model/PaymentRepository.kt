package com.uniandes.modus.model

import com.uniandes.modus.getCacheDataBase
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class PaymentRepository private constructor() {

    private val supabase = SupabaseConfig.instance.client
    private val dbCache = getCacheDataBase()!!

    companion object {
        val instance: PaymentRepository by lazy { PaymentRepository() }
    }

    @Serializable
    data class Payment(
        @SerialName("user_document") val userDocument: String,
        val year: String,
        val week: String,
        val state: String,
        val month: String,
        val day: String,
    )

    suspend fun getYearPaymentsByUser(userDocument: String, year: String): List<Payment> {
        return try {

            val remote = supabase.postgrest["payments"]
                .select {
                    filter {
                        eq(column = "user_document", value = userDocument)
                        eq(column = "year", value = year)
                    }
                }
                .decodeList<Payment>()

            dbCache.transaction {
                remote.forEach {
                    dbCache.paymentCacheQueries.insertOrReplace(
                        user_document = it.userDocument,
                        year = it.year,
                        week = it.week,
                        month = it.month,
                        day = it.day,
                        state = it.state
                    )
                }
            }

            remote
        } catch (e: Exception) {
            e.printStackTrace()
            dbCache.paymentCacheQueries.selectByUserYear(userDocument, year)
                .executeAsList()
                .map {
                    Payment(
                        userDocument = it.user_document,
                        year = it.year,
                        week = it.week,
                        month = it.month,
                        day = it.day,
                        state = it.state
                    )
                }
        }
    }

    suspend fun updateStateOfPayment(userDocument: String, year: String, week: String, state: String): Boolean {
        return try {
            supabase.postgrest["payments"].update (
                {
                    set(column = "state", value = state)
                }
            ) {
                filter {
                    eq(column = "user_document", value = userDocument)
                    eq(column = "year", value = year)
                    eq(column = "week", value =  week)
                }
            }

            dbCache.paymentCacheQueries.updateState(state, userDocument, year, week)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun createPaymentsOfYear(userDocument: String, year: String, week: String): Boolean {
        return try {
            val yearInt = year.toInt()
            val currentWeek = week.toInt()

            val firstDayOfYear = LocalDate(yearInt, 1, 1)

            var firstSunday = firstDayOfYear
            if (firstSunday.dayOfWeek != DayOfWeek.SUNDAY) {
                val daysToAdd = 7 - firstSunday.dayOfWeek.isoDayNumber
                firstSunday = firstSunday.plus(DatePeriod(days = daysToAdd))
            }

            val payments = mutableListOf<Payment>()

            var sundayDate = firstSunday
            var weekNumber = 1
            while (sundayDate.year == yearInt) {
                val state = when {
                    weekNumber < currentWeek -> "5"
                    weekNumber == currentWeek -> "2"
                    else -> "6"
                }

                val monthStr = sundayDate.monthNumber.toString()
                val dayStr = sundayDate.dayOfMonth.toString()

                payments.add(
                    Payment(
                        userDocument = userDocument,
                        year = year,
                        week = weekNumber.toString(),
                        state = state,
                        month = monthStr,
                        day = dayStr
                    )
                )

                sundayDate = sundayDate.plus(DatePeriod(days = 7))
                weekNumber++
            }

            supabase.postgrest["payments"].insert(payments)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}