package com.uniandes.modus.ui.games.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.uniandes.modus.BlackColor
import com.uniandes.modus.BlackLightColor
import com.uniandes.modus.BlueColor
import com.uniandes.modus.GrayColor
import com.uniandes.modus.GrayLightColor
import com.uniandes.modus.WhiteBeige2Color
import com.uniandes.modus.WhiteBeigeColor
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.WinnerGoldColor
import com.uniandes.modus.brushBlackBackGround
import com.uniandes.modus.brushWhiteBackGround
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Inter_ExtraBold
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.arrow_back
import modus.composeapp.generated.resources.icon_no_internet
import modus.composeapp.generated.resources.week
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class GamesDetailUserScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { GamesUserViewModel() }

        navigatorContent(viewModel, navigator)
        gamesDetailUserContent(viewModel)
    }
}

@Composable
private fun navigatorContent(viewModel: GamesUserViewModel, navigator: Navigator) {
    val navigateToGames = viewModel.navigateToGamesFromGamesDetailUser.collectAsState().value

    LaunchedEffect(navigateToGames) {
        if (navigateToGames) {
            navigator.push(GamesUserScreen())
            viewModel.resetNavigation()
        }
    }
}

@Composable
private fun gamesDetailUserContent(viewModel: GamesUserViewModel) {
    val isScreenGamesDetailLoading = viewModel.isScreenGamesDetailLoading.collectAsState().value
    val isDeviceWhitOutConnection = viewModel.isDeviceWhitOutConnectionScreenDetailGames.collectAsState().value

    Column (modifier = Modifier.fillMaxSize().background(brush = brushWhiteBackGround), verticalArrangement = Arrangement.Top) {
        HeaderSection(viewModel)

        if (isDeviceWhitOutConnection) {
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
        } else if (isScreenGamesDetailLoading) {
            Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator(color = BlackLightColor, strokeWidth = 2.dp, modifier = Modifier.size(40.dp).padding(5.dp))
                Text(
                    text = "Loading",
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = BlackLightColor
                )
            }
        }
        else {
            StreamSection(viewModel)
            OptionsSection(viewModel)
        }
    }
}

@Composable
private fun HeaderSection(viewModel: GamesUserViewModel) {
    val weekSelected = viewModel.weekSelected.collectAsState().value

    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp, end = 20.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToGamesFromGamesDetailUserScreen() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.arrow_back),
                tint = WhiteColor
            )
        }
        Text(
            text = "Bingo" + " - ",
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteColor
        )
        Text(
            text = stringResource(Res.string.week) + " " + weekSelected,
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteBeigeColor
        )
    }
}

@Composable
expect fun StreamSection(viewModel: GamesUserViewModel)

@Composable
private fun OptionsSection(viewModel: GamesUserViewModel) {
    var cardSectionPress by remember { mutableStateOf(true) }
    var numbersSelectedSectionPress by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth().height(50.dp)) {
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    cardSectionPress = true
                    numbersSelectedSectionPress = false
                },
            shape = RoundedCornerShape(0.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteColor)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().let { if (cardSectionPress) it.background(brushBlackBackGround) else it },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Card",
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = if (cardSectionPress) WhiteColor else BlackColor
                )
            }
        }
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    cardSectionPress = false
                    numbersSelectedSectionPress = true
                },
            shape = RoundedCornerShape(0.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteColor)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().let { if (numbersSelectedSectionPress) it.background(brushBlackBackGround) else it },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Numbers Selected",
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = if (numbersSelectedSectionPress) WhiteColor else BlackColor
                )
            }
        }
    }

    if (cardSectionPress) {
        CardSection(viewModel)
    } else if (numbersSelectedSectionPress) {
        NumbersSelectedSection(viewModel)
    }
}

@Composable
private fun CardSection(viewModel: GamesUserViewModel) {
    val gameSelected = viewModel.gameSelected.collectAsState().value
    val cardSelected = viewModel.cardSelected.collectAsState().value

    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {

        Row(modifier = Modifier.fillMaxWidth().height(50.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("B", "I", "N", "G", "O").forEach { letter ->
                numberItem(
                    text = letter,
                    backgroundColor = GrayColor,
                    textColor = GrayLightColor,
                    textSize = 20.sp,
                    modifier = Modifier.weight(1f).fillMaxHeight())
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        cardSelected?.let { card ->
            val sortedNumbers = card.numbers.entries.sortedBy { it.value.firstOrNull()?.toIntOrNull() ?: 0 }
            val rows = sortedNumbers.chunked(5)

            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                rows.forEach { numberGroup ->
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        numberGroup.forEach { (key, values) ->
                            val backgroundColor = when {
                                (gameSelected?.winners?.isNotEmpty() == true && values[1] == "true") -> WinnerGoldColor
                                (values[1] == "true") -> BlueColor
                                else -> GrayLightColor
                            }
                            val textColor = if (values[1] == "true") WhiteColor else GrayColor

                            if (key == "M") {
                                numberItem(
                                    text = key,
                                    backgroundColor = WhiteBeige2Color,
                                    textColor = WhiteColor,
                                    textSize = 20.sp,
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                )
                            } else {
                                numberItem(
                                    text = key,
                                    backgroundColor = backgroundColor,
                                    textColor = textColor,
                                    textSize = 16.sp,
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun numberItem(text: String, backgroundColor: Color, textColor: Color, textSize: TextUnit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = textSize,
                fontFamily = FontFamily(Font(Res.font.Inter_ExtraBold)),
                color = textColor
            )
        }
    }
}

@Composable
private fun NumbersSelectedSection(viewModel: GamesUserViewModel) {
    val gameSelected = viewModel.gameSelected.collectAsState().value

    gameSelected?.let { game ->
        val sortedNumbers = game.numbersSelected.entries.sortedByDescending { it.key.toIntOrNull() ?: 0 }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(15.dp)) {
            items(items = sortedNumbers.chunked(6)) { numberGroup ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    numberGroup.forEach { (_, number) ->
                        var formatNumber = ""
                        when {
                            number.toInt() <= 15 -> formatNumber = "B$number"
                            number.toInt() <= 30 -> formatNumber = "I$number"
                            number.toInt() <= 45 -> formatNumber = "N$number"
                            number.toInt() <= 60 -> formatNumber = "G$number"
                            number.toInt() <= 75 -> formatNumber = "O$number"
                        }
                        numberSelectedItem(
                            modifier = Modifier.weight(1f),
                            number = formatNumber
                        )
                    }
                    repeat(6 - numberGroup.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun numberSelectedItem(modifier: Modifier, number: String) {
    Card(
        modifier = modifier.aspectRatio(1f).padding(5.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteBeige2Color)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_ExtraBold)),
                color = WhiteColor
            )
        }
    }
}