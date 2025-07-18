package com.uniandes.modus

import androidx.compose.runtime.Composable
 import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.uniandes.modus.model.AuthenticationRepository
import com.uniandes.modus.ui.home.HomeScreen
import com.uniandes.modus.ui.login.LoginScreen
import androidx.compose.runtime.*

@Composable
fun App() {
    var keyCounter by remember { mutableStateOf(0) }

    val isLoggedIn by AuthenticationRepository.instance.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        keyCounter++
    }

    key(keyCounter) {
        AppRoot()
    }
}

@Composable
fun AppRoot() {
    val startScreen = if (AuthenticationRepository.instance.getCurrentUser() != null) HomeScreen() else LoginScreen()
    Navigator(screen = startScreen) { navigator ->
        FadeTransition(navigator)
    }
}