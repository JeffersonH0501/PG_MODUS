package com.uniandes.modus.ui.games.admin

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
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
import com.uniandes.modus.getNowLocalDateTime
import com.uniandes.modus.getSundayOfWeek
import com.uniandes.modus.getTodayLocalDate
import com.uniandes.modus.ui.home.HomeScreen
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Inter_ExtraBold
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.arrow_back
import modus.composeapp.generated.resources.arrow_forward
import modus.composeapp.generated.resources.icon_short_arrow_right
import modus.composeapp.generated.resources.year
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class GamesAdminScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { GamesAdminViewModel() }

        navigatorContent(viewModel, navigator)
        gamesAdminContent(viewModel)
    }

}

@Composable
private fun navigatorContent(viewModel: GamesAdminViewModel, navigator: Navigator) {
    val navigateToHome = viewModel.navigateToHomeFromGamesAdmin.collectAsState().value
    val navigateToGamesDetail = viewModel.navigateToGamesDetailFromGamesAdmin.collectAsState().value

    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {
            navigator.push(HomeScreen())
            viewModel.resetNavigation()
        }
    }

    LaunchedEffect(navigateToGamesDetail) {
        if (navigateToGamesDetail) {
            navigator.push(GamesDetailAdminScreen())
            viewModel.resetNavigation()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun gamesAdminContent(viewModel: GamesAdminViewModel) {
    val isScreenGamesLoading = viewModel.isScreenGamesLoading.collectAsState().value
    val isScreenGamesRefreshing = viewModel.isScreenGamesRefreshing.collectAsState().value

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
        } else {
            PullToRefreshBox (isRefreshing = isScreenGamesRefreshing, onRefresh = { viewModel.refreshScreenGamesAdmin() }) {
                ListOfGamesSection(viewModel)
            }
        }
    }
}

@Composable
private fun HeaderSection(viewModel: GamesAdminViewModel) {
    val year = viewModel.yearSelected.collectAsState().value

    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToHomeFromGamesAdminScreen() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.arrow_back),
                tint = WhiteColor
            )
        }
        Text(
            text = "Bingos" + " - ",
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteColor
        )
        Text(
            text = stringResource(Res.string.year) + " " + year,
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteBeigeColor
        )
    }
}

@Composable
private fun ListOfGamesSection(viewModel: GamesAdminViewModel) {
    val games = viewModel.games.collectAsState().value

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 15.dp, vertical = 15.dp)) {
        items(games.sortedByDescending { it.week.toInt() }) { game ->
            gameItem(
                year = game.year,
                week = game.week,
                onClick = { viewModel.navigateToGamesDetailFromGamesAdminScreen(game) }
            )
        }
    }
}

@Composable
private fun gameItem(year: String, week: String, onClick: () -> Unit) {
    val currentDate = getTodayLocalDate()
    val dateOfWeek = getSundayOfWeek(year.toInt(), week.toInt())
    val now = getNowLocalDateTime()

    val state = when {
        dateOfWeek < currentDate -> "Finalizado"
        dateOfWeek == currentDate -> {
            val currentHour = now.hour
            when {
                currentHour < 21 -> "Se juega hoy"
                currentHour in 21..21 -> "En Juego"
                else -> "Finalizado"
            }
        }
        else -> {
            val daysUntil = dateOfWeek.toEpochDays() - currentDate.toEpochDays()
            "En $daysUntil días"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).height(70.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = WhiteColor,
            contentColor = BlackColor
        )
    ) {
        Row (modifier = Modifier.fillMaxSize().padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {

            Card(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(width = 1.5.dp, color = BlackColor),
                elevation = CardDefaults.cardElevation(0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = WhiteColor,
                    contentColor = BlackColor
                )
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = week,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.Inter_ExtraBold)),
                        color = BlackColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(15.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bingo Week $week",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = BlackColor,
                    lineHeight = 16.sp
                )
                Text(
                    text = state,
                    fontSize = 16.sp,
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