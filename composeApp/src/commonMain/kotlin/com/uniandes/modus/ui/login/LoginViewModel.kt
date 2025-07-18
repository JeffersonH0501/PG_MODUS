package com.uniandes.modus.ui.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.uniandes.modus.getConnectivityChecker
import com.uniandes.modus.model.AuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel: ScreenModel {

    private val authenticationRepository = AuthenticationRepository.instance

    private val _document = mutableStateOf(value = "")
    val document: State<String> = _document

    private val _password = mutableStateOf(value = "")
    val password: State<String> = _password

    private val _documentError = mutableStateOf(value = false)
    val documentError: State<Boolean> = _documentError

    private val _passwordError = mutableStateOf(value = false)
    val passwordError: State<Boolean> = _passwordError

    private val _messageError = mutableStateOf(value = "")
    val messageError: State<String> = _messageError

    private val _isLoading = MutableStateFlow(value = false)
    val isLoading: StateFlow<Boolean> = _isLoading

    sealed class LoginNavigationEvent {
        data object None : LoginNavigationEvent()
        data object NavigateToHome : LoginNavigationEvent()
        data object NavigateToRegister : LoginNavigationEvent()
    }

    private val _navigationEvent = MutableStateFlow<LoginNavigationEvent>(LoginNavigationEvent.None)
    val navigationEvent: StateFlow<LoginNavigationEvent> = _navigationEvent

    fun updateDocument(newText: String) {
        _document.value = newText
    }

    fun updatePassword(newText: String) {
        _password.value = newText
    }

    fun validateLogin() {
        _messageError.value = ""
        _documentError.value = false
        _passwordError.value = false

        when {
            document.value.trim().isEmpty() -> {
                _messageError.value = "Document cannot be empty"
                _documentError.value = true
                return
            }

            password.value.trim().isEmpty() -> {
                _messageError.value = "Password cannot be empty"
                _passwordError.value = true
                return
            }
        }

        screenModelScope.launch {
            if (getConnectivityChecker().isInternetAvailable()) {
                _isLoading.value = true
                val isAuthenticated = authenticationRepository.authenticate(document.value, password.value)
                _isLoading.value = false

                if (isAuthenticated) {
                    _navigationEvent.value = LoginNavigationEvent.NavigateToHome
                } else {
                    _messageError.value = "The document or password is incorrect"
                }
            } else {
                _messageError.value = "The device is not connected to the internet"
            }
        }
    }

    fun navigateToRegisterScreen() {
        _navigationEvent.value = LoginNavigationEvent.NavigateToRegister
    }

    fun resetNavigation() {
        _navigationEvent.value = LoginNavigationEvent.None
    }
}