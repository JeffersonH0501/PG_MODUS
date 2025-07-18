package com.uniandes.modus.ui.payments.user

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
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
import com.uniandes.modus.GreenColor
import com.uniandes.modus.RedColor
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.OrangeColor
import com.uniandes.modus.WhiteBeigeColor
import com.uniandes.modus.brushBlackBackGround
import com.uniandes.modus.brushWhiteBackGround
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Inter_ExtraBold
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.arrow_back
import modus.composeapp.generated.resources.arrow_forward
import modus.composeapp.generated.resources.current_week
import modus.composeapp.generated.resources.icon_no_internet
import modus.composeapp.generated.resources.icon_payments
import modus.composeapp.generated.resources.icon_reload
import modus.composeapp.generated.resources.icon_short_arrow_right
import modus.composeapp.generated.resources.missing
import modus.composeapp.generated.resources.paid
import modus.composeapp.generated.resources.payments
import modus.composeapp.generated.resources.under_review
import modus.composeapp.generated.resources.unpaid
import modus.composeapp.generated.resources.weeks_summary
import modus.composeapp.generated.resources.year
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class PaymentsUserScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { PaymentsUserViewModel() }

        navigatorContent(viewModel, navigator)
        paymentsUserContent(viewModel)
    }

}

@Composable
private fun navigatorContent(viewModel: PaymentsUserViewModel, navigator: Navigator) {
    val navigateToHome = viewModel.navigateToHomeFromPaymentsUser.collectAsState().value
    val navigateToPaymentsDetail = viewModel.navigateToPaymentsDetailFromPaymentsUser.collectAsState().value

    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {
            viewModel.resetNavigation()
            navigator.pop()
        }
    }

    LaunchedEffect(navigateToPaymentsDetail) {
        if (navigateToPaymentsDetail) {
            viewModel.resetNavigation()
            navigator.push(PaymentsDetailUserScreen())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun paymentsUserContent(viewModel: PaymentsUserViewModel) {
    val isScreenPaymentsLoading = viewModel.isScreenPaymentsLoading.collectAsState().value
    val isScreenPaymentsRefreshing = viewModel.isScreenPaymentsRefreshing.collectAsState().value
    val isDeviceWhitOutConnection = viewModel.isDeviceWhitOutConnection.collectAsState().value

    Column(
        modifier = Modifier.fillMaxSize().background(brush = brushWhiteBackGround),
        verticalArrangement = Arrangement.Top
    ) {
        HeaderSection(viewModel)

        if (isScreenPaymentsLoading) {
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
        } else {
            PullToRefreshBox (isRefreshing = isScreenPaymentsRefreshing, onRefresh = { viewModel.refreshScreenPayments() }) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    WeeksSummarySection(viewModel)
                    CurrentWeekSection(viewModel)
                }
            }
            ListOfWeeksSection(viewModel)
        }
    }
}

@Composable
private fun HeaderSection(viewModel: PaymentsUserViewModel) {
    val year = viewModel.yearSelected.collectAsState().value

    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp, end = 10.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToHomeScreenFromPaymentsUser() }) {
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
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { viewModel.refreshScreenPayments() }) {
            Icon(
                painter = painterResource(Res.drawable.icon_reload),
                contentDescription = stringResource(Res.string.arrow_back),
                tint = WhiteColor
            )
        }
    }
}

@Composable
private fun WeeksSummarySection(viewModel: PaymentsUserViewModel) {
    val paidWeeks = viewModel.paidWeeks.collectAsState().value
    val unpaidWeeks = viewModel.unpaidWeeks.collectAsState().value
    val missingWeeks = viewModel.missingWeeks.collectAsState().value

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            modifier = Modifier.padding(vertical = 20.dp),
            text = stringResource(Res.string.weeks_summary),
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = BlackColor
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            WeekSummaryItem(count = paidWeeks, label = stringResource(Res.string.paid))
            WeekSummaryItem(count = unpaidWeeks, label = stringResource(Res.string.unpaid))
            WeekSummaryItem(count = missingWeeks, label = stringResource(Res.string.missing))
        }
    }
}

@Composable
fun WeekSummaryItem(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_ExtraBold)),
            color = BlackLightColor
        )
        Text(
            text = label,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = BlackLightColor
        )
    }
}

@Composable
private fun CurrentWeekSection(viewModel: PaymentsUserViewModel) {
    val currentPayment = viewModel.currentPayment.collectAsState().value

    val currentWeekStateString = when (currentPayment?.state ?: "") {
        "1" -> stringResource(Res.string.paid)
        "3" -> stringResource(Res.string.under_review)
        else -> stringResource(Res.string.unpaid)
    }

    val backgroundColor = when (currentPayment?.state ?: "") {
        "1" -> GreenColor
        "2" -> RedColor
        else -> OrangeColor
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 20.dp).height(70.dp),
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
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = currentPayment?.week ?: "",
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
                    text = currentWeekStateString,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = when (currentPayment?.state) {
                        "1" -> GreenColor
                        "3" -> OrangeColor
                        else -> RedColor
                    },
                    lineHeight = 16.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxHeight().clickable { viewModel.navigateToPaymentsDetailScreenFromPaymentsUser(currentPayment) },
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
private fun ListOfWeeksSection(viewModel: PaymentsUserViewModel) {
    val payments = viewModel.payments.collectAsState().value

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp).padding(top = 5.dp, bottom = 20.dp)) {
        items(items = payments.chunked(size = 6)) { weekGroup ->
            Row(modifier = Modifier.fillMaxWidth()) {
                weekGroup.forEach { payment ->
                    WeekItem(
                        modifier = Modifier.weight(1f),
                        week = payment.week,
                        state = payment.state,
                        onClick = { viewModel.navigateToPaymentsDetailScreenFromPaymentsUser(payment) },
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
private fun WeekItem(modifier: Modifier, week: String, state: String, onClick: () -> Unit) {
    val backgroundColor = when (state) {
        "1" -> GreenColor
        "2" -> RedColor
        "3" -> OrangeColor
        "4" -> RedColor
        "5" -> GrayColor
        "6" -> GrayLightColor
        else -> Color.White
    }

    val textColor = when (state) {
        "1" -> WhiteColor
        "2" -> WhiteColor
        "3" -> WhiteColor
        "4" -> WhiteColor
        "5" -> GrayLightColor
        "6" -> GrayColor
        else -> BlackColor
    }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(5.dp)
            .let { if (state != "5" && state != "6") it.clickable { onClick() } else it },
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