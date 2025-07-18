package com.uniandes.modus.ui.payments.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.uniandes.modus.BlackColor
import com.uniandes.modus.BlackLightColor
import com.uniandes.modus.GreenColor
import com.uniandes.modus.RedColor
import com.uniandes.modus.OrangeColor
import com.uniandes.modus.WhiteBeigeColor
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.brushBlackBackGround
import com.uniandes.modus.brushWhiteBackGround
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.april
import modus.composeapp.generated.resources.arrow_back
import modus.composeapp.generated.resources.august
import modus.composeapp.generated.resources.day
import modus.composeapp.generated.resources.december
import modus.composeapp.generated.resources.february
import modus.composeapp.generated.resources.icon_no_internet
import modus.composeapp.generated.resources.january
import modus.composeapp.generated.resources.july
import modus.composeapp.generated.resources.june
import modus.composeapp.generated.resources.march
import modus.composeapp.generated.resources.may
import modus.composeapp.generated.resources.month
import modus.composeapp.generated.resources.november
import modus.composeapp.generated.resources.october
import modus.composeapp.generated.resources.paid
import modus.composeapp.generated.resources.september
import modus.composeapp.generated.resources.state
import modus.composeapp.generated.resources.under_review
import modus.composeapp.generated.resources.unpaid
import modus.composeapp.generated.resources.week
import modus.composeapp.generated.resources.year
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class PaymentsDetailUserScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { PaymentsUserViewModel() }

        navigationContent(viewModel, navigator)
        paymentsDetailUserContent(viewModel)
    }
}

@Composable
private fun navigationContent(viewModel: PaymentsUserViewModel, navigator: Navigator) {
    val navigateToPayments = viewModel.navigateToPaymentsFromPaymentsDetailUser.collectAsState().value

    LaunchedEffect(navigateToPayments) {
        if (navigateToPayments) {
            viewModel.resetNavigation()
            navigator.pop()
        }
    }
}

@Composable
private fun paymentsDetailUserContent(viewModel: PaymentsUserViewModel) {
    val isScreenPaymentsDetailLoading = viewModel.isScreenPaymentsDetailLoading.collectAsState().value

    Column(
        modifier = Modifier.fillMaxSize().background(brush = brushWhiteBackGround),
        verticalArrangement = Arrangement.Top
    ) {
        HeaderSection(viewModel)

        if (isScreenPaymentsDetailLoading) {
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
            WeekSummary(viewModel)
            VoucherUser(viewModel)
            showDialog(viewModel)
        }
    }
}

@Composable
private fun HeaderSection(viewModel: PaymentsUserViewModel) {
    val paymentSelected = viewModel.paymentSelected.collectAsState().value

    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToPaymentsScreenFromPaymentsDetailUser() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.arrow_back),
                tint = WhiteColor
            )
        }
        Text(
            text = "Payment - ",
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteColor
        )
        Text(
            text = stringResource(Res.string.week) + " " + paymentSelected?.week,
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteBeigeColor
        )
    }
}

@Composable
private fun WeekSummary(viewModel: PaymentsUserViewModel) {
    val paymentSelected = viewModel.paymentSelected.collectAsState().value

    val monthString = when (paymentSelected?.month ?: "") {
        "1" -> stringResource(Res.string.january)
        "2" -> stringResource(Res.string.february)
        "3" -> stringResource(Res.string.march)
        "4" -> stringResource(Res.string.april)
        "5" -> stringResource(Res.string.may)
        "6" -> stringResource(Res.string.june)
        "7" -> stringResource(Res.string.july)
        "8" -> stringResource(Res.string.august)
        "9" -> stringResource(Res.string.september)
        "10" -> stringResource(Res.string.october)
        "11" -> stringResource(Res.string.november)
        "12" -> stringResource(Res.string.december)
        else -> ""
    }

    val weekStateString = when (paymentSelected?.state ?: "") {
        "1" -> stringResource(Res.string.paid)
        "3" -> stringResource(Res.string.under_review)
        else -> stringResource(Res.string.unpaid)
    }

    Text(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
        textAlign = TextAlign.Center,
        text = "Information",
        fontSize = 20.sp,
        fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
        color = BlackColor
    )

    InfoRow(label = stringResource(Res.string.day) + ":", value = paymentSelected?.day ?: "", color = BlackLightColor)
    InfoRow(label = stringResource(Res.string.month) + ":", value = monthString, color = BlackLightColor)
    InfoRow(label = stringResource(Res.string.year) + ":", value = paymentSelected?.year ?: "", color = BlackLightColor)
    InfoRow(label = stringResource(Res.string.state) + ":", value = weekStateString,
        color = if (paymentSelected!!.state == "1") GreenColor else if (paymentSelected.state == "3") OrangeColor else RedColor
    )

    Spacer(modifier = Modifier.height(20.dp))
    HorizontalDivider(modifier = Modifier.fillMaxWidth().height(1.dp))
}

@Composable
private fun InfoRow(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                color = BlackColor
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Text(
            text = value,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
expect fun VoucherUser(viewModel: PaymentsUserViewModel)

@Composable
fun showDialog(viewModel: PaymentsUserViewModel) {
    val showDialogWithoutConnection = viewModel.showDialogDeviceWhitOutConnection.collectAsState().value

    if (showDialogWithoutConnection) {
        AlertDialog(
            containerColor = Color(color = 0xFFfaf7f5),
            shape = RoundedCornerShape(8.dp),
            onDismissRequest = { viewModel.hideShowDialogDeviceWhitOutConnection() },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(100.dp).padding(10.dp),
                        painter = painterResource(Res.drawable.icon_no_internet),
                        contentDescription = "icon_no_internet",
                        tint = BlackColor
                    )
                    Text(
                        text = "Action Failed",
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                        lineHeight = 20.sp,
                        color = BlackColor
                    )
                    Text(
                        text = "No internet connection",
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                        lineHeight = 20.sp,
                        color = BlackLightColor
                    )
                }
            },
            confirmButton = {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    Spacer(modifier = Modifier.weight(0.5f))
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp)
                            .clickable(onClick = { viewModel.hideShowDialogDeviceWhitOutConnection() }),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = BlackColor)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Accept",
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                                color = WhiteColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(0.5f))
                }
            }
        )
    }
}