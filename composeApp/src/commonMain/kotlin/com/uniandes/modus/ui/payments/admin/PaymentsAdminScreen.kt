package com.uniandes.modus.ui.payments.admin

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.uniandes.modus.BlackColor
import com.uniandes.modus.BlackLightColor
import com.uniandes.modus.GrayColor
import com.uniandes.modus.GrayLightColor
import com.uniandes.modus.OrangeColor
import com.uniandes.modus.WhiteBeige2Color
import com.uniandes.modus.WhiteBeigeColor
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.brushBlackBackGround
import com.uniandes.modus.brushWhiteBackGround
import com.uniandes.modus.ui.home.HomeScreen
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Inter_ExtraBold
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.arrow_back
import modus.composeapp.generated.resources.arrow_forward
import modus.composeapp.generated.resources.current_week
import modus.composeapp.generated.resources.icon_short_arrow_right
import modus.composeapp.generated.resources.payments
import modus.composeapp.generated.resources.year
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class PaymentsAdminScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { PaymentsAdminViewModel() }

        navigatorContent(viewModel, navigator)
        paymentsAdminContent(viewModel)
    }
}

@Composable
private fun navigatorContent(viewModel: PaymentsAdminViewModel, navigator: Navigator) {
    val navigateToHome = viewModel.navigateToHomeFromPaymentsAdmin.collectAsState().value
    val navigateToPaymentsDetail = viewModel.navigateToPaymentsDetailFromPaymentsAdmin.collectAsState().value

    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {
            navigator.push(HomeScreen())
            viewModel.resetNavigation()
        }
    }

    LaunchedEffect(navigateToPaymentsDetail) {
        if (navigateToPaymentsDetail) {
            navigator.push(PaymentsDetailAdminScreen())
            viewModel.resetNavigation()
        }
    }
}

@Composable
private fun paymentsAdminContent(viewModel: PaymentsAdminViewModel) {
    Column (
        modifier = Modifier.fillMaxSize().background(brush = brushWhiteBackGround),
        verticalArrangement = Arrangement.Top
    ) {
        HeaderSection(viewModel)
        CurrentWeek(viewModel)
        ListOfWeeks(viewModel)
    }
}

@Composable
private fun HeaderSection(viewModel: PaymentsAdminViewModel) {
    val year = viewModel.yearSelected.collectAsState().value

    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToHomeScreen() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.arrow_back),
                tint = WhiteColor
            )
        }
        Text(
            text = stringResource(Res.string.payments) + " - ",
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
private fun CurrentWeek(viewModel: PaymentsAdminViewModel) {
    val currentWeek = viewModel.currentWeek.collectAsState().value
    val numberOfPendingReviews = viewModel.selectedNumberOfPendingReviews.collectAsState().value

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp).padding(bottom = 15.dp, top = 20.dp).height(70.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteColor)
    ) {
        Row (modifier = Modifier.fillMaxSize().padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {

            Card(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(0.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteBeige2Color)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = currentWeek,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.Inter_ExtraBold)),
                        color = WhiteColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(15.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.current_week),
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = BlackColor,
                    lineHeight = 16.sp
                )
                Text(
                    text = when (numberOfPendingReviews.toIntOrNull() ?: 0) {
                        0 -> "No Pending Reviews"
                        1 -> "1 Review Pending"
                        else -> "$numberOfPendingReviews Reviews pending"
                    },
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = if ((numberOfPendingReviews.toIntOrNull() ?: 0) > 0) OrangeColor else BlackLightColor,
                    lineHeight = 16.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxHeight().clickable { viewModel.navigateToPaymentsDetailFromPaymentsAdminScreen(currentWeek) },
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

@Composable
private fun ListOfWeeks(viewModel: PaymentsAdminViewModel) {
    val currentWeek = viewModel.currentWeek.collectAsState().value
    val weeksMap = viewModel.reviews.collectAsState().value

    val sortedWeeks = weeksMap.entries
        .sortedBy { it.key.toIntOrNull() ?: 0 }
        .map { it.toPair() }

    val weekGroups = sortedWeeks.chunked(6)

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp).padding(bottom = 20.dp)) {
        items(items = weekGroups) { weekGroup ->
            Row(modifier = Modifier.fillMaxWidth()) {
                weekGroup.forEach { (weekNumber, _) ->
                    WeekItem(
                        week = weekNumber,
                        isCurrentWeek = weekNumber.toInt() == currentWeek.toInt(),
                        isFutureWeek = weekNumber.toInt() > currentWeek.toInt(),
                        onClick = { viewModel.navigateToPaymentsDetailFromPaymentsAdminScreen(weekNumber) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(6 - weekGroup.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun WeekItem(week: String, isCurrentWeek: Boolean, isFutureWeek: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {

    val backgroundColor = when {
        !isCurrentWeek && !isFutureWeek -> WhiteColor
        isCurrentWeek -> WhiteBeige2Color
        else -> GrayLightColor
    }

    val textColor = when {
        !isCurrentWeek && !isFutureWeek -> BlackColor
        isCurrentWeek -> WhiteColor
        else -> GrayColor
    }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(5.dp)
            .let { if (!isFutureWeek) it.clickable { onClick() } else it },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = week,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_ExtraBold)),
                color = textColor
            )
        }
    }
}