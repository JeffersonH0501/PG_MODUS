package com.uniandes.modus.ui.games.user

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.uniandes.modus.getConnectivityChecker
import com.uniandes.modus.getCurrentWeek
import com.uniandes.modus.getCurrentYear
import com.uniandes.modus.model.AuthenticationRepository
import com.uniandes.modus.model.CardRepository
import com.uniandes.modus.model.GameRepository
import io.github.jan.supabase.realtime.RealtimeChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GamesUserViewModel: ScreenModel {

    private val authenticationRepository = AuthenticationRepository.instance
    private val gameRepository = GameRepository.instance
    private val cardRepository = CardRepository.instance

    private val _yearSelected = MutableStateFlow("")
    val yearSelected: StateFlow<String> = _yearSelected

    private val _weekSelected = MutableStateFlow("")
    val weekSelected: StateFlow<String> = _weekSelected

    private val _cards = MutableStateFlow<List<CardRepository.Card>>(emptyList())
    val cards: StateFlow<List<CardRepository.Card>> = _cards

    private val _cardSelected = MutableStateFlow<CardRepository.Card?>(null)
    val cardSelected: StateFlow<CardRepository.Card?> = _cardSelected

    private val _gameSelected = MutableStateFlow<GameRepository.Game?>(null)
    val gameSelected: StateFlow<GameRepository.Game?> = _gameSelected

    private val _isScreenGamesLoading = MutableStateFlow(true)
    val isScreenGamesLoading: StateFlow<Boolean> = _isScreenGamesLoading

    private val _isScreenGamesRefreshing = MutableStateFlow(false)
    val isScreenGamesRefreshing: StateFlow<Boolean> = _isScreenGamesRefreshing

    private val _isScreenGamesDetailLoading = MutableStateFlow(true)
    val isScreenGamesDetailLoading: StateFlow<Boolean> = _isScreenGamesDetailLoading

    private val _navigateToHomeFromGamesUser = MutableStateFlow(false)
    val navigateToHomeFromGamesUser: StateFlow<Boolean> = _navigateToHomeFromGamesUser

    private val _navigateToGamesDetailFromGamesUser = MutableStateFlow(false)
    val navigateToGamesDetailFromGamesUser: StateFlow<Boolean> = _navigateToGamesDetailFromGamesUser

    private val _navigateToGamesFromGamesDetailUser = MutableStateFlow(false)
    val navigateToGamesFromGamesDetailUser: StateFlow<Boolean> = _navigateToGamesFromGamesDetailUser

    private val _isDeviceWhitOutConnectionScreenGames = MutableStateFlow(false)
    val isDeviceWhitOutConnectionScreenGames: StateFlow<Boolean> = _isDeviceWhitOutConnectionScreenGames

    private val _isDeviceWhitOutConnectionScreenDetailGames = MutableStateFlow(false)
    val isDeviceWhitOutConnectionScreenDetailGames: StateFlow<Boolean> = _isDeviceWhitOutConnectionScreenDetailGames

    private val _isCheckingConnectionScreenDetailGames = MutableStateFlow(false)
    val isCheckingConnectionScreenDetailGames: StateFlow<Boolean> = _isCheckingConnectionScreenDetailGames

    private var gameChannel: RealtimeChannel? = null
    private var cardChannel: RealtimeChannel? = null

    private suspend fun loadData() {
        _yearSelected.value = getCurrentYear().toString()

        _cards.value = authenticationRepository.getCurrentUser()?.let { user ->
            cardRepository.getYearCardsByUser(user.document, _yearSelected.value)
                .sortedBy { it.week.toInt() }
        } ?: emptyList()

        _isDeviceWhitOutConnectionScreenGames.value = _cards.value.isEmpty()
    }

    init {
        screenModelScope.launch {
            loadData()
            _isScreenGamesLoading.value = false
        }
    }

    fun refreshScreenGamesUser() {
        screenModelScope.launch {
            _isScreenGamesRefreshing.value = true
            loadData()
            _isScreenGamesRefreshing.value = false
        }
    }

    fun navigateToHomeFromGamesUserScreen() {
        _navigateToHomeFromGamesUser.value = true
    }

    fun navigateToGamesDetailFromGamesUserScreen(card: CardRepository.Card) {
        _isScreenGamesDetailLoading.value = true
        _weekSelected.value = card.week

        screenModelScope.launch {
            _navigateToGamesDetailFromGamesUser.value = true

            _gameSelected.value = gameRepository.getGameByYearWeek(card.year, card.week)
            _cardSelected.value = card

            _isDeviceWhitOutConnectionScreenDetailGames.value = _gameSelected.value == null

            if (getCurrentWeek() == card.week.toInt() && getCurrentYear() == card.year.toInt()) {
                gameChannel = gameRepository.subscribeToGame(
                    year = card.year,
                    week = card.week,
                    coroutineScope = screenModelScope
                ) { updatedGame ->
                    _gameSelected.value = updatedGame
                }

                cardChannel = cardRepository.subscribeToCard(
                    userDocument = card.userDocument,
                    year = card.year,
                    week = card.week,
                    coroutineScope = screenModelScope
                ) { updatedCard ->
                    _cardSelected.value = updatedCard
                    _cards.value = _cards.value.map {
                        if (it.userDocument == updatedCard.userDocument && it.year == updatedCard.year && it.week == updatedCard.week) {
                            updatedCard
                        } else {
                            it
                        }
                    }
                }
            }

            _isScreenGamesDetailLoading.value = false

            getConnectivityChecker().observeConnectivity().collect { isConnected ->
                if (isConnected) {
                    _gameSelected.value = gameRepository.getGameByYearWeek(card.year, card.week)
                    _cardSelected.value = cardRepository.getCardByUserYearWeek(card.userDocument, card.year, card.week)
                    _isCheckingConnectionScreenDetailGames.value = true
                    _isDeviceWhitOutConnectionScreenDetailGames.value = false
                } else {
                    _isCheckingConnectionScreenDetailGames.value = false
                }
            }
        }
    }

    fun navigateToGamesFromGamesDetailUserScreen() {
        _navigateToGamesFromGamesDetailUser.value = true
        screenModelScope.launch {
            gameChannel?.unsubscribe()
            gameChannel = null

            cardChannel?.unsubscribe()
            cardChannel = null
        }
    }

    fun resetNavigation() {
        _navigateToHomeFromGamesUser.value = false
        _navigateToGamesDetailFromGamesUser.value = false
        _navigateToGamesFromGamesDetailUser.value = false
    }
}