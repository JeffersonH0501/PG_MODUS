package com.uniandes.modus.ui.games.admin

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
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
import modus.composeapp.generated.resources.Inter_Medium
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.arrow_back
import modus.composeapp.generated.resources.arrow_forward
import modus.composeapp.generated.resources.icon_short_arrow_right
import modus.composeapp.generated.resources.week
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class GamesDetailAdminScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { GamesAdminViewModel() }

        navigatorContent(viewModel, navigator)
        gamesDetailAdminContent(viewModel)
    }
}

@Composable
private fun navigatorContent(viewModel: GamesAdminViewModel, navigator: Navigator) {
    val navigateToGames = viewModel.navigateToGamesFromGamesDetailAdmin.collectAsState().value

    LaunchedEffect(navigateToGames) {
        if (navigateToGames) {
            navigator.push(GamesAdminScreen())
            viewModel.resetNavigation()
        }
    }
}

@Composable
private fun gamesDetailAdminContent(viewModel: GamesAdminViewModel) {
    val isScreenGamesDetailLoading = viewModel.isScreenGamesDetailLoading.collectAsState().value

    Column(
        modifier = Modifier.fillMaxSize().background(brush = brushWhiteBackGround),
        verticalArrangement = Arrangement.Top
    ) {
        HeaderSection(viewModel)

        if (isScreenGamesDetailLoading) {
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
            OptionsSection(viewModel)
        }

    }
}

@Composable
private fun HeaderSection(viewModel: GamesAdminViewModel) {
    val week = viewModel.weekSelected.collectAsState().value

    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToGamesFromGamesDetailAdminScreen() }) {
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
            text = stringResource(Res.string.week) + " " + week,
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteBeigeColor
        )
    }
}

@Composable
private fun OptionsSection(viewModel: GamesAdminViewModel) {
    var unselectedPress by remember { mutableStateOf(true) }
    var selectedPress by remember { mutableStateOf(false) }
    var winnersPress by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth().height(50.dp)) {
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    unselectedPress = true
                    selectedPress = false
                    winnersPress = false
                },
            shape = RoundedCornerShape(0.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = if (unselectedPress) BlackColor else WhiteColor)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Unselected",
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_ExtraBold)),
                    color = if (unselectedPress) WhiteColor else BlackColor
                )
            }
        }
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    unselectedPress = false
                    selectedPress = true
                    winnersPress = false
                },
            shape = RoundedCornerShape(0.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = if (selectedPress) BlackColor else WhiteColor)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Selected",
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_ExtraBold)),
                    color = if (selectedPress) WhiteColor else BlackColor
                )
            }
        }
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    unselectedPress = false
                    selectedPress = false
                    winnersPress = true
                },
            shape = RoundedCornerShape(0.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = if (winnersPress) BlackColor else WhiteColor)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Winners",
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_ExtraBold)),
                    color = if (winnersPress) WhiteColor else BlackColor
                )
            }
        }
    }

    if (unselectedPress) {
        NumbersNotSelectedSection(viewModel)
    } else if (selectedPress) {
        NumbersSelectedSection(viewModel)
    } else if (winnersPress) {
        WinnersSection(viewModel)
    }
}

@Composable
private fun NumbersNotSelectedSection(viewModel: GamesAdminViewModel) {
    val gameSelected = viewModel.gameSelected.collectAsState().value
    var pendingNumber by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    gameSelected?.let { game ->
        val numbers = (1..75).map { it.toString() }.filter { it -> it !in game.numbersSelected.values.map { it } }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(15.dp)) {
            items(items = numbers.chunked(6)) { numberGroup ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    numberGroup.forEach { number ->
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
                            number = formatNumber,
                            backgroundColor = if (gameSelected.winners.isEmpty()) WhiteBeige2Color else GrayLightColor,
                            textColor = if (gameSelected.winners.isEmpty()) WhiteColor else GrayColor,
                            onClick = {
                                pendingNumber = number
                                showDialog = true
                            }
                        )
                    }
                    repeat(6 - numberGroup.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    if (showDialog && gameSelected?.winners?.isEmpty() == true) {
        AddDialog(
            number = pendingNumber,
            onDismiss = { showDialog = false },
            onConfirm = {
                viewModel.addNumberToGame(pendingNumber)
                pendingNumber = ""
                showDialog = false
            }
        )
    }
}

@Composable
private fun AddDialog(number: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    var formatNumber = ""
    when {
        number.toInt() <= 15 -> formatNumber = "B$number"
        number.toInt() <= 30 -> formatNumber = "I$number"
        number.toInt() <= 45 -> formatNumber = "N$number"
        number.toInt() <= 60 -> formatNumber = "G$number"
        number.toInt() <= 75 -> formatNumber = "O$number"
    }

    AlertDialog(
        containerColor = Color(color = 0xFFfaf7f5),
        shape = RoundedCornerShape(8.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Number",
                fontSize = 20.sp,
                lineHeight = 25.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                color = BlackColor
            )
        },
        text = {
            Text(
                text = buildAnnotatedString {
                    append("Do you want add number ")
                    withStyle(style = SpanStyle(fontFamily = FontFamily(Font(Res.font.Inter_Bold)))) {
                        append(formatNumber)
                    }
                    append("?")
                },
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                color = BlackLightColor
            )
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.weight(1f).height(45.dp).clickable(onClick = { onConfirm() }),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = BlueColor)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Add",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                            color = WhiteColor
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    modifier = Modifier.weight(1f).height(45.dp).clickable(onClick = { onDismiss() }),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = BlackColor)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                            color = WhiteColor
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun NumbersSelectedSection(viewModel: GamesAdminViewModel) {
    val gameSelected = viewModel.gameSelected.collectAsState().value

    gameSelected?.let { game ->
        val numbers = game.numbersSelected.entries.sortedByDescending { it.key.toIntOrNull() ?: 0 }.map { it }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(15.dp)) {
            items(items = numbers.chunked(6)) { numberGroup ->
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
                            number = formatNumber,
                            backgroundColor = if (gameSelected.winners.isEmpty()) BlueColor else WinnerGoldColor,
                            textColor = WhiteColor,
                            onClick = { }
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
private fun numberSelectedItem(modifier: Modifier, number: String, backgroundColor: Color, textColor: Color, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(5.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_ExtraBold)),
                color = textColor
            )
        }
    }
}

@Composable
private fun WinnersSection(viewModel: GamesAdminViewModel) {
    val winners = viewModel.gameWinners.collectAsState().value

    if (winners.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 15.dp, vertical = 15.dp)) {
            items(winners) { winner ->
                WinnerItem(
                    name = winner.user?.name ?: "User Name Not Found",
                    document = winner.userDocument,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun WinnerItem(name: String, document: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).height(70.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteColor)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(start = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = BlackColor
                )
                Text(
                    text = document,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = BlackLightColor
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