package com.uniandes.modus.ui.garments.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.brushBlackBackGround
import com.uniandes.modus.brushWhiteBackGround
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.arrow_back
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource

class StoresLocationScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { GarmentsUserViewModel() }

        navigatorContent(viewModel, navigator)
        storesLocationContent(viewModel)
    }

}

@Composable
private fun navigatorContent(viewModel: GarmentsUserViewModel, navigator: Navigator) {
    val navigationEvent = viewModel.navigationEvent.collectAsState().value

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is GarmentsUserViewModel.GarmentsNavigationEvent.NavigateToGarmentsFromStoresLocation -> {
                viewModel.resetNavigation()
                navigator.pop()
            }
            else -> Unit
        }
    }

}

@Composable
private fun storesLocationContent(viewModel: GarmentsUserViewModel) {

    Column (
        modifier = Modifier.fillMaxSize().background(brush = brushWhiteBackGround),
        verticalArrangement = Arrangement.Top
    ) {
        HeaderSection(viewModel)
        MapSection(viewModel)
    }

}

@Composable
private fun HeaderSection(viewModel: GarmentsUserViewModel) {
    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp, end = 10.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToGarmentsScreenFromStoresLocationScreen()  }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.arrow_back),
                tint = WhiteColor
            )
        }
        Text(
            text = "Our stores",
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteColor
        )
    }
}

@Composable
expect fun MapSection(viewModel: GarmentsUserViewModel)