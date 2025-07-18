package com.uniandes.modus.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.uniandes.modus.model.AuthenticationRepository
import com.uniandes.modus.model.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel: ScreenModel {

    private val authenticationRepository = AuthenticationRepository.instance

    private val _currentUser = MutableStateFlow<UserRepository.User?>(null)
    val currentUser: StateFlow<UserRepository.User?> = _currentUser

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin

    private val _navigateToPayments = MutableStateFlow(false)
    val navigateToPayments: StateFlow<Boolean> = _navigateToPayments

    private val _navigateToGames = MutableStateFlow(false)
    val navigateToGames: StateFlow<Boolean> = _navigateToGames

    private val _navigateToCatalog = MutableStateFlow(false)
    val navigateToCatalog: StateFlow<Boolean> = _navigateToCatalog

    private val _navigateToAccount = MutableStateFlow(false)
    val navigateToAccount: StateFlow<Boolean> = _navigateToAccount

    init {
        screenModelScope.launch {
            val user = authenticationRepository.getCurrentUser()

            if (user == null) {
                _navigateToLogin.value = true
            } else {
                _currentUser.value = user
                _userName.value = user.name.split(" ").firstOrNull() ?: user.name
            }
        }
    }

    fun navigateToPaymentsScreen() {
        _navigateToPayments.value = true
    }

    fun navigateToBingosScreen() {
        _navigateToGames.value = true
    }

    fun navigateToCatalogScreen() {
        _navigateToCatalog.value = true
    }

    fun navigateToAccountScreen() {
        _navigateToAccount.value = true
    }

    fun resetNavigation() {
        _navigateToLogin.value = false
        _navigateToPayments.value = false
        _navigateToGames.value = false
        _navigateToCatalog.value = false
        _navigateToAccount.value = false
    }
}