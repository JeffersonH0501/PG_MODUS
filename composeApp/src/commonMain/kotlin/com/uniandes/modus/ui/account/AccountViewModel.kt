package com.uniandes.modus.ui.account

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.uniandes.modus.model.AuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountViewModel: ScreenModel {

    private val authenticationRepository = AuthenticationRepository.instance

    private val _navigateToHomeFromAccount = MutableStateFlow(false)
    val navigateToHomeFromAccount: StateFlow<Boolean> = _navigateToHomeFromAccount

    private val _navigateToLoginFromAccount = MutableStateFlow(false)
    val navigateToLoginFromAccount: StateFlow<Boolean> = _navigateToLoginFromAccount

    fun logOut() {
        screenModelScope.launch {
            if (authenticationRepository.logOut()) {
                _navigateToLoginFromAccount.value = true
            }
        }
    }

    fun navigateToHomeScreen() {
        _navigateToHomeFromAccount.value = true
    }

    fun resetNavigation() {
        _navigateToHomeFromAccount.value = false
        _navigateToLoginFromAccount.value = false
    }
}