package com.uniandes.modus.model

import com.uniandes.modus.getCacheDataBase
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class CardRepository private constructor() {

    private val supabase = SupabaseConfig.instance.client
    private val dbCache = getCacheDataBase()!!

    companion object {
        val instance: CardRepository by lazy { CardRepository() }
    }

    @Serializable
    data class Card(
        @SerialName("user_document") val userDocument: String,
        @SerialName("users") val user: UserRepository.User? = null,
        val year: String,
        val week: String,
        val numbers: Map<String, List<String>>
    )

    // ****************************** ADMIN VIEW **************************************************************************************************************************

    suspend fun createCard(userDocument: String, year: String, week: String): Boolean {
        return try {
            val numbersMap = mutableMapOf<String, List<String>>()

            val columnB = (1..15).shuffled().take(5).map { it.toString() }
            val columnI = (16..30).shuffled().take(5).map { it.toString() }
            val columnN = (31..45).shuffled().take(4).map { it.toString() }
            val columnG = (46..60).shuffled().take(5).map { it.toString() }
            val columnO = (61..75).shuffled().take(5).map { it.toString() }

            val positions = listOf(
                listOf("1", "6", "11", "16", "21"),
                listOf("2", "7", "12", "17", "22"),
                listOf("3", "8", "18", "23"),
                listOf("4", "9", "14", "19", "24"),
                listOf("5", "10", "15", "20", "25")
            )

            val allColumns = listOf(columnB, columnI, columnN, columnG, columnO)
            for ((colIndex, numbers) in allColumns.withIndex()) {
                for ((numIndex, number) in numbers.withIndex()) {
                    numbersMap[number] = listOf(positions[colIndex][numIndex], "false")
                }
            }

            numbersMap["M"] = listOf("13", "true")

            val card = Card(
                userDocument = userDocument,
                year = year,
                week = week,
                numbers = numbersMap
            )

            supabase.postgrest["cards"].insert(card)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getCardByUserDocumentYearWeek(userDocument: String, year: String, week: String): Card? {
        val columns = Columns.raw("""
        user_document, year, week, numbers,
        users (name, email, phone, department, city, address, role, document)
        """.trimIndent())

        return try {
            supabase.postgrest["cards"].select(columns = columns) {
                filter {
                    eq(column = "user_document", value = userDocument)
                    eq(column = "year", value = year)
                    eq(column = "week", value = week)
                }
            }.decodeSingleOrNull<Card>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateCardsForNumberByGame(year: String, week: String, number: String): Boolean {
        return try {
            val params = buildJsonObject {
                put("p_year", year)
                put("p_week", week)
                put("p_number", number)
            }
            supabase.postgrest.rpc(function = "update_cards_number", parameters = params)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ****************************** USER VIEW **************************************************************************************************************************

    suspend fun getCardByUserYearWeek(userDocument: String, year: String, week: String): Card? {
        return try {
            val card = supabase.postgrest["cards"]
                .select {
                    filter {
                        eq(column = "user_document", value = userDocument)
                        eq(column = "year", value = year)
                        eq(column = "week", value = week)
                    }
                }.decodeSingleOrNull<Card>()

            dbCache.cardCacheQueries.insertOrReplace(
                user_document = card!!.userDocument,
                year = card.year,
                week = card.week,
                numbers = Json.encodeToString(card.numbers)
            )

            card
        } catch (e: Exception) {
            e.printStackTrace()
            dbCache.cardCacheQueries.selectCardByUser(
                user_document = userDocument,
                year = year,
                week = week
            ).executeAsOneOrNull()?.let {
                Card(
                    userDocument = it.user_document,
                    year = it.year,
                    week = it.week,
                    numbers = Json.decodeFromString<Map<String, List<String>>>(it.numbers)
                )
            }
        }
    }

    suspend fun getYearCardsByUser(userDocument: String, year: String): List<Card> {
        return try {
            val cards = supabase.postgrest["cards"]
                .select {
                    filter {
                        eq(column = "user_document", value = userDocument)
                        eq(column = "year", value = year)
                    }
                }
                .decodeList<Card>()

            dbCache.transaction {
                cards.forEach {
                    dbCache.cardCacheQueries.insertOrReplace(
                        user_document = it.userDocument,
                        year = it.year,
                        week = it.week,
                        numbers = Json.encodeToString(it.numbers)
                    )
                }
            }

            cards
        } catch (e: Exception) {
            e.printStackTrace()
            dbCache.cardCacheQueries.selectYearCardsByUser(userDocument, year)
                .executeAsList()
                .map {
                    Card(
                        userDocument = it.user_document,
                        year = it.year,
                        week = it.week,
                        numbers = Json.decodeFromString<Map<String, List<String>>>(it.numbers)
                    )
                }
        }
    }

    suspend fun subscribeToCard(userDocument: String, year: String, week: String, coroutineScope: CoroutineScope, onCardUpdate: (Card) -> Unit): RealtimeChannel {
        val channel = supabase.channel(channelId = "card_update_channel") { }

        val cardUpdateFlow = channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
            table = "cards"
            filter(column = "user_document", operator = FilterOperator.EQ, value = userDocument)
            filter(column = "year", operator = FilterOperator.EQ, value = year)
            filter(column = "week", operator = FilterOperator.EQ, value = week)
        }.map { event ->
            event.decodeRecord<Card>()
        }

        cardUpdateFlow.onEach { updatedCard ->
            if (updatedCard.userDocument == userDocument && updatedCard.year == year && updatedCard.week == week) {
                dbCache.cardCacheQueries.insertOrReplace(
                    user_document = updatedCard.userDocument,
                    year = updatedCard.year,
                    week = updatedCard.week,
                    numbers = Json.encodeToString(updatedCard.numbers)
                )
                onCardUpdate(updatedCard)
            }
        }.launchIn(coroutineScope)

        channel.subscribe()
        return channel
    }

}