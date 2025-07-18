package com.uniandes.modus.ui.home

import com.uniandes.modus.ui.account.AccountScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import com.uniandes.modus.BlackColor
import com.uniandes.modus.BlackLightColor
import com.uniandes.modus.WhiteBeigeColor
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.brushBlackBackGround
import com.uniandes.modus.brushWhiteBackGround
import com.uniandes.modus.getTimeOfDay
import com.uniandes.modus.ui.games.admin.GamesAdminScreen
import com.uniandes.modus.ui.games.user.GamesUserScreen
import com.uniandes.modus.ui.garments.user.GarmentsUserScreen
import com.uniandes.modus.ui.login.LoginScreen
import com.uniandes.modus.ui.payments.admin.PaymentsAdminScreen
import com.uniandes.modus.ui.payments.user.PaymentsUserScreen
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Inter_Medium
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.account
import modus.composeapp.generated.resources.account_description
import modus.composeapp.generated.resources.bingo
import modus.composeapp.generated.resources.bingo_description
import modus.composeapp.generated.resources.catalog
import modus.composeapp.generated.resources.catalog_description
import modus.composeapp.generated.resources.greeting
import modus.composeapp.generated.resources.icon_account
import modus.composeapp.generated.resources.icon_bingos
import modus.composeapp.generated.resources.icon_catalog
import modus.composeapp.generated.resources.icon_payments
import modus.composeapp.generated.resources.options
import modus.composeapp.generated.resources.payments
import modus.composeapp.generated.resources.payments_description
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class HomeScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { HomeViewModel() }

        navigationContent(viewModel, navigator)
        HomeContent(viewModel)
    }

}

@Composable
private fun navigationContent(viewModel: HomeViewModel, navigator: Navigator) {
    val currentUser = viewModel.currentUser.collectAsState().value

    val navigateToLogin = viewModel.navigateToLogin.collectAsState().value
    val navigateToPayments = viewModel.navigateToPayments.collectAsState().value
    val navigateToGames = viewModel.navigateToGames.collectAsState().value
    val navigateToCatalog = viewModel.navigateToCatalog.collectAsState().value
    val navigateToAccount = viewModel.navigateToAccount.collectAsState().value

    LaunchedEffect(navigateToLogin) {
        if (navigateToLogin) {
            viewModel.resetNavigation()
            navigator.replaceAll(LoginScreen())
        }
    }

    LaunchedEffect(navigateToPayments) {
        if (navigateToPayments && currentUser?.role == "User") {
            viewModel.resetNavigation()
            navigator.push(PaymentsUserScreen())
        } else if (navigateToPayments && currentUser?.role == "Admin") {
            viewModel.resetNavigation()
            navigator.push(PaymentsAdminScreen())
        }
    }

    LaunchedEffect(navigateToGames) {
        if (navigateToGames && currentUser?.role == "User") {
            navigator.push(GamesUserScreen())
            viewModel.resetNavigation()
        } else if (navigateToGames && currentUser?.role == "Admin") {
            navigator.push(GamesAdminScreen())
            viewModel.resetNavigation()
        }
    }

    LaunchedEffect(navigateToCatalog) {
        if (navigateToCatalog) {
            navigator.push(GarmentsUserScreen())
            viewModel.resetNavigation()
        }
    }

    LaunchedEffect(navigateToAccount) {
        if (navigateToAccount) {

            println("${navigator.size}: ${navigator.items}")
            navigator.push(AccountScreen())
            viewModel.resetNavigation()
        }
    }
}

@Composable
private fun HomeContent(viewModel: HomeViewModel) {
    Column (modifier = Modifier.fillMaxSize().background(brush = brushWhiteBackGround), verticalArrangement = Arrangement.Top){
        Header(viewModel)
        Options(viewModel)
    }
}

@Composable
private fun Header(viewModel: HomeViewModel) {
    val userName = viewModel.userName.collectAsState().value

    Column(
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.greeting) + " " + userName +",",
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteBeigeColor
        )
        Text(
            text = getTimeOfDay(),
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteColor
        )
    }
}

@Composable
private fun Options(viewModel: HomeViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.options),
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = BlackColor
        )
    }

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp).height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(15.dp)) {
        OptionCard(
            icon = painterResource(Res.drawable.icon_payments),
            title = stringResource(Res.string.payments),
            description = stringResource(Res.string.payments_description),
            onClick = { viewModel.navigateToPaymentsScreen() },
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
        OptionCard(
            icon = painterResource(Res.drawable.icon_bingos),
            title = stringResource(Res.string.bingo),
            description = stringResource(Res.string.bingo_description),
            onClick = { viewModel.navigateToBingosScreen() },
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
    }

    Spacer(modifier = Modifier.height(15.dp))

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp).height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(15.dp)) {
        OptionCard(
            icon = painterResource(Res.drawable.icon_catalog),
            title = stringResource(Res.string.catalog),
            description = stringResource(Res.string.catalog_description),
            onClick = { viewModel.navigateToCatalogScreen() },
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
        OptionCard(
            icon = painterResource(Res.drawable.icon_account),
            title = stringResource(Res.string.account),
            description = stringResource(Res.string.account_description),
            onClick = { viewModel.navigateToAccountScreen() },
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
    }
}

@Composable
fun OptionCard(icon: Painter, title: String, description: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = WhiteColor,
            contentColor = BlackColor
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = icon,
                    contentDescription = title,
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = BlackColor
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = description,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                color = BlackLightColor,
                lineHeight = 16.sp
            )
        }
    }
}