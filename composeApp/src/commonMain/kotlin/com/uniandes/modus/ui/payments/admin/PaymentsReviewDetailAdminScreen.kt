package com.uniandes.modus.ui.payments.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.OrangeColor
import com.uniandes.modus.WhiteBeigeColor
import com.uniandes.modus.brushBlackBackGround
import com.uniandes.modus.brushWhiteBackGround
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.arrow_back
import modus.composeapp.generated.resources.week
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource

class PaymentsReviewDetailAdminScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { PaymentsAdminViewModel() }

        navigatorContent(viewModel, navigator)
        paymentsWeekDetailAdminContent(viewModel)
    }

}

@Composable
private fun navigatorContent(viewModel: PaymentsAdminViewModel, navigator: Navigator) {
    val navigateToPaymentsDetail = viewModel.navigateToPaymentsDetailFromReviewDetail.collectAsState().value

    LaunchedEffect(navigateToPaymentsDetail) {
        if (navigateToPaymentsDetail) {
            navigator.push(PaymentsDetailAdminScreen())
            viewModel.resetNavigation()
        }
    }
}

@Composable
private fun paymentsWeekDetailAdminContent(viewModel: PaymentsAdminViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().background(brush = brushWhiteBackGround),
        verticalArrangement = Arrangement.Top
    ) {
        HeaderSection(viewModel)
        InformationSection(viewModel)
        VoucherAdminSection(viewModel)
    }
}

@Composable
private fun HeaderSection(viewModel: PaymentsAdminViewModel) {
    val week = viewModel.weekSelected.collectAsState().value

    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToPaymentsDetailFromReviewDetailScreen() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.arrow_back),
                tint = WhiteColor
            )
        }
        Text(
            text = "Review" + " - ",
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
private fun InformationSection(viewModel: PaymentsAdminViewModel) {
    val reviewSelected = viewModel.reviewSelected.collectAsState().value

    val reviewStateString = when (reviewSelected?.state ?: "") {
        "1" -> "Approved"
        "2" -> "Rejected"
        else -> "Awaiting Review"
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Information",
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = BlackColor
        )
    }

    InfoRow(label = "Name" + ":",  value = reviewSelected!!.user!!.name, color = BlackLightColor)
    InfoRow(label = "Document" + ":", value = reviewSelected.userDocument, color = BlackLightColor)
    InfoRow(label = "State" + ":", value = reviewStateString,
        color = if (reviewSelected.state == "1") GreenColor else if (reviewSelected.state == "2") RedColor else OrangeColor
    )
    Spacer(modifier = Modifier.height(20.dp))
    HorizontalDivider(modifier = Modifier.fillMaxWidth().height(1.dp))
}

@Composable
private fun InfoRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            textAlign = TextAlign.Start
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = color,
            textAlign = TextAlign.End
        )
    }
}

@Composable
expect fun VoucherAdminSection(viewModel: PaymentsAdminViewModel)