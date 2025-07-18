package com.uniandes.modus.ui.games.admin

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.uniandes.modus.getCurrentYear
import com.uniandes.modus.model.CardRepository
import com.uniandes.modus.model.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GamesAdminViewModel: ScreenModel {

    private val gameRepository = GameRepository.instance
    private val cardRepository = CardRepository.instance

    private val _yearSelected = MutableStateFlow(value = "")
    val yearSelected: StateFlow<String> = _yearSelected

    private val _weekSelected = MutableStateFlow(value = "")
    val weekSelected: StateFlow<String> = _weekSelected

    private val _games = MutableStateFlow<List<GameRepository.Game>>(emptyList())
    val games: StateFlow<List<GameRepository.Game>> = _games

    private val _gameSelected = MutableStateFlow<GameRepository.Game?>(value = null)
    val gameSelected: StateFlow<GameRepository.Game?> = _gameSelected

    private val _gameWinners = MutableStateFlow<List<CardRepository.Card>>(emptyList())
    val gameWinners: StateFlow<List<CardRepository.Card>> = _gameWinners

    private val _isScreenGamesLoading = MutableStateFlow(value = true)
    val isScreenGamesLoading: StateFlow<Boolean> = _isScreenGamesLoading

    private val _isScreenGamesRefreshing = MutableStateFlow(value = false)
    val isScreenGamesRefreshing: StateFlow<Boolean> = _isScreenGamesRefreshing

    private val _isScreenGamesDetailLoading = MutableStateFlow(value = true)
    val isScreenGamesDetailLoading: StateFlow<Boolean> = _isScreenGamesDetailLoading

    private val _navigateToHomeFromGamesAdmin = MutableStateFlow(value = false)
    val navigateToHomeFromGamesAdmin: StateFlow<Boolean> = _navigateToHomeFromGamesAdmin

    private val _navigateToGamesDetailFromGamesAdmin = MutableStateFlow(value = false)
    val navigateToGamesDetailFromGamesAdmin: StateFlow<Boolean> = _navigateToGamesDetailFromGamesAdmin

    private val _navigateToGamesFromGamesDetailAdmin = MutableStateFlow(value = false)
    val navigateToGamesFromGamesDetailAdmin: StateFlow<Boolean> = _navigateToGamesFromGamesDetailAdmin

    private suspend fun loadData() {
        _yearSelected.value = getCurrentYear().toString()

        _games.value = gameRepository.getGamesByYear(_yearSelected.value)
    }

    init {
        screenModelScope.launch {
            loadData()
            _isScreenGamesLoading.value = false
        }
    }

    fun refreshScreenGamesAdmin() {
        screenModelScope.launch {
            _isScreenGamesRefreshing.value = true
            loadData()
            _isScreenGamesRefreshing.value = false
        }
    }

    fun addNumberToGame(number: String) {
        screenModelScope.launch {
            _gameSelected.value?.let { game ->
                val updatedNumbers = game.numbersSelected.toMutableMap().apply {
                    put(key = (game.numbersSelected.size + 1).toString(), value = number)
                }
                gameRepository.updateNumbersSelectedOfGame(year = game.year, week = game.week, numbersSelected = updatedNumbers)
                cardRepository.updateCardsForNumberByGame(year = game.year, week = game.week, number = number)
                gameRepository.updateWinnersByGame(year = game.year, week = game.week)
                _gameSelected.value = gameRepository.getGameByYearWeek(year = game.year, week = game.week)
                _gameSelected.value?.winners?.forEach { document ->
                    cardRepository.getCardByUserDocumentYearWeek(
                        userDocument = document,
                        year = game.year,
                        week = game.week
                    )?.let { card ->
                        _gameWinners.value += card
                    }
                }
            }
        }
    }

    fun navigateToHomeFromGamesAdminScreen() {
        _navigateToHomeFromGamesAdmin.value = true
    }

    fun navigateToGamesDetailFromGamesAdminScreen(game: GameRepository.Game) {
        _navigateToGamesDetailFromGamesAdmin.value = true
        _gameSelected.value = game
        _weekSelected.value = game.week

        screenModelScope.launch {
            _gameWinners.value = emptyList()
            _gameSelected.value?.winners?.forEach { document ->
                cardRepository.getCardByUserDocumentYearWeek(
                    userDocument = document,
                    year = game.year,
                    week = game.week
                )?.let { card ->
                    _gameWinners.value += card
                }
            }
        }

        _isScreenGamesDetailLoading.value = false
    }

    fun navigateToGamesFromGamesDetailAdminScreen() {
        _navigateToGamesFromGamesDetailAdmin.value = true
    }

    fun resetNavigation() {
        _navigateToHomeFromGamesAdmin.value = false
        _navigateToGamesDetailFromGamesAdmin.value = false
        _navigateToGamesFromGamesDetailAdmin.value = false
    }
}