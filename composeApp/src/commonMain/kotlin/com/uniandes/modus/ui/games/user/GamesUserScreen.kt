package com.uniandes.modus.ui.games.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.uniandes.modus.BlackColor
import com.uniandes.modus.BlackLightColor
import com.uniandes.modus.WhiteBeigeColor
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.brushBlackBackGround
import com.uniandes.modus.brushWhiteBackGround
import com.uniandes.modus.getSundayOfWeek
import com.uniandes.modus.ui.home.HomeScreen
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.arrow_back
import modus.composeapp.generated.resources.arrow_forward
import modus.composeapp.generated.resources.icon_no_internet
import modus.composeapp.generated.resources.icon_reload
import modus.composeapp.generated.resources.icon_short_arrow_right
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class GamesUserScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { GamesUserViewModel() }

        navigatorContent(viewModel, navigator)
        gamesUserContent(viewModel)
    }

}

@Composable
private fun navigatorContent(viewModel: GamesUserViewModel, navigator: Navigator) {
    val navigateToHome = viewModel.navigateToHomeFromGamesUser.collectAsState().value
    val navigateToGamesDetail = viewModel.navigateToGamesDetailFromGamesUser.collectAsState().value

    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {
            navigator.push(HomeScreen())
            viewModel.resetNavigation()
        }
    }

    LaunchedEffect(navigateToGamesDetail) {
        if (navigateToGamesDetail) {
            navigator.push(GamesDetailUserScreen())
            viewModel.resetNavigation()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun gamesUserContent(viewModel: GamesUserViewModel) {
    val isScreenGamesLoading = viewModel.isScreenGamesLoading.collectAsState().value
    val isScreenGamesRefreshing = viewModel.isScreenGamesRefreshing.collectAsState().value
    val isDeviceWhitOutConnection = viewModel.isDeviceWhitOutConnectionScreenGames.collectAsState().value

    Column(
        modifier = Modifier.fillMaxSize().background(brush = brushWhiteBackGround),
        verticalArrangement = Arrangement.Top
    ) {
        HeaderSection(viewModel)

        if (isScreenGamesLoading) {
            Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator(color = BlackLightColor, strokeWidth = 2.dp, modifier = Modifier.size(40.dp).padding(5.dp))
                Text(
                    text = "Loading",
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = BlackLightColor
                )
            }
        } else if (isDeviceWhitOutConnection) {
            Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(
                    modifier = Modifier.size(100.dp).padding(5.dp),
                    painter = painterResource(Res.drawable.icon_no_internet),
                    contentDescription = "icon_no_internet",
                    tint = BlackLightColor
                )
                Text(
                    text = "No internet connection",
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = BlackLightColor
                )
            }
        }
        else {
            PullToRefreshBox (isRefreshing = isScreenGamesRefreshing, onRefresh = { viewModel.refreshScreenGamesUser() }) {
                ListOfCardsSection(viewModel)
            }
        }
    }
}

@Composable
private fun HeaderSection(viewModel: GamesUserViewModel) {
    val year = viewModel.yearSelected.collectAsState().value

    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp, end = 10.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToHomeFromGamesUserScreen() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Arrow Back",
                tint = WhiteColor
            )
        }
        Text(
            text = "Bingos - ",
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteColor
        )
        Text(
            text = year,
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteBeigeColor
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { viewModel.refreshScreenGamesUser() }) {
            Icon(
                painter = painterResource(Res.drawable.icon_reload),
                contentDescription = stringResource(Res.string.arrow_back),
                tint = WhiteColor
            )
        }
    }
}

@Composable
private fun ListOfCardsSection(viewModel: GamesUserViewModel) {
    val cards = viewModel.cards.collectAsState().value

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 15.dp, vertical = 15.dp)) {
        items(cards.sortedByDescending { it.week.toInt() }) { card ->
            CardItem(
                year = card.year,
                week = card.week,
                onClick = { viewModel.navigateToGamesDetailFromGamesUserScreen(card) }
            )
        }
    }
}

@Composable
private fun CardItem(year: String, week: String, onClick: () -> Unit) {
    val sundayStreaming = getSundayOfWeek(year.toInt(), week.toInt())
    val state = "${sundayStreaming.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${sundayStreaming.dayOfMonth}"

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).height(70.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteColor)
    ) {
        Row (
            modifier = Modifier.fillMaxSize().padding(start = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bingo Week $week",
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = BlackColor,
                    lineHeight = 16.sp
                )
                Text(
                    text = state,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = BlackLightColor,
                    lineHeight = 16.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxHeight().clickable { onClick() },
                shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 8.dp, bottomEnd = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = WhiteColor,
                    contentColor = BlackColor
                )
            ) {
                Box(modifier = Modifier.fillMaxHeight().padding(horizontal = 15.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        painter = painterResource(Res.drawable.icon_short_arrow_right),
                        contentDescription = stringResource(Res.string.arrow_forward)
                    )
                }
            }
        }
    }
}