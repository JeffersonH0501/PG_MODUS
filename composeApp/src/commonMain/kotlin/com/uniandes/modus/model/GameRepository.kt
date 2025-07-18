package com.uniandes.modus.model

import com.uniandes.modus.getCacheDataBase
import io.github.jan.supabase.postgrest.postgrest
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

class GameRepository private constructor() {

    private val supabase = SupabaseConfig.instance.client
    private val dbCache = getCacheDataBase()!!

    companion object {
        val instance: GameRepository by lazy { GameRepository() }
    }

    @Serializable
    data class Game(
        val year: String,
        val week: String,
        val month: String,
        val day: String,
        @SerialName("numbers_selected") val numbersSelected: Map<String, String>,
        val winners: List<String>,
        @SerialName("stream_id") val streamId: String?
    )

    // ****************************** ADMIN VIEW **************************************************************************************************************************

    suspend fun getGamesByYear(year: String): List<Game> {
        return try {
            supabase.postgrest["games"]
                .select {
                    filter {
                        eq(column = "year", value = year)
                    }
                }
                .decodeList<Game>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun updateNumbersSelectedOfGame(year: String, week: String, numbersSelected: Map<String, String>): Boolean {
        return try {
            supabase.postgrest["games"].update (
                {
                    set(column = "numbers_selected", value = numbersSelected)
                }
            ) {
                filter {
                    eq(column = "year", value = year)
                    eq(column = "week", value =  week)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateWinnersByGame(year: String, week: String): Boolean {
        return try {
            val params = buildJsonObject {
                put("p_year", year)
                put("p_week", week)
            }
            supabase.postgrest.rpc(function = "update_winners_by_game", parameters = params)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ****************************** USER VIEW **************************************************************************************************************************

    suspend fun getGameByYearWeek(year: String, week: String): Game? {
        return try {
            val game = supabase.postgrest["games"]
                .select {
                    filter {
                        eq(column = "year", value = year)
                        eq(column = "week", value = week)
                    }
                }
                .decodeSingleOrNull<Game>()

            dbCache.gameCacheQueries.insertOrReplace(
                year = game!!.year,
                week = game.week,
                month = game.month,
                day = game.day,
                numbers_selected = Json.encodeToString(game.numbersSelected),
                winners = Json.encodeToString(game.winners),
                stream_id = game.streamId
            )

            game
        } catch (e: Exception) {
            e.printStackTrace()
            dbCache.gameCacheQueries.selectGameByYearWeek(
                year = year,
                week = week
            ).executeAsOneOrNull()?.let {
                Game(
                    year = it.year,
                    week = it.week,
                    month = it.month,
                    day = it.day,
                    numbersSelected = Json.decodeFromString<Map<String, String>>(it.numbers_selected),
                    winners = Json.decodeFromString<List<String>>(it.winners),
                    streamId = it.stream_id
                )
            }
        }
    }

    suspend fun subscribeToGame(year: String, week: String, coroutineScope: CoroutineScope, onGameUpdate: (Game) -> Unit): RealtimeChannel {
        val channel = supabase.channel(channelId = "game_update_channel") { }

        val gameUpdateFlow = channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
            table = "games"
            filter(column = "year", operator = FilterOperator.EQ, value = year)
            filter(column = "week", operator = FilterOperator.EQ, value = week)
        }.map { event ->
            event.decodeRecord<Game>()
        }

        gameUpdateFlow.onEach { updatedGame ->
            if (updatedGame.year == year && updatedGame.week == week) {
                dbCache.gameCacheQueries.insertOrReplace(
                    year = updatedGame.year,
                    week = updatedGame.week,
                    month = updatedGame.month,
                    day = updatedGame.day,
                    numbers_selected = Json.encodeToString(updatedGame.numbersSelected),
                    winners = Json.encodeToString(updatedGame.winners),
                    stream_id = updatedGame.streamId
                )
                onGameUpdate(updatedGame)
            }
        }.launchIn(coroutineScope)

        channel.subscribe()
        return channel
    }
}