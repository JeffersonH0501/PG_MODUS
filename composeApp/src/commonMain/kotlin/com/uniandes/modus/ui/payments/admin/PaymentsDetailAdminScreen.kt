package com.uniandes.modus.ui.payments.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
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
import com.uniandes.modus.ErrorColor
import com.uniandes.modus.GreenDarkColor
import com.uniandes.modus.WhiteBeige2Color
import com.uniandes.modus.WhiteBeigeColor
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.alpha_0_4
import com.uniandes.modus.alpha_0_7
import com.uniandes.modus.brushBlackBackGround
import com.uniandes.modus.brushWhiteBackGround
import com.uniandes.modus.model.ReviewRepository
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.arrow_back
import modus.composeapp.generated.resources.arrow_forward
import modus.composeapp.generated.resources.icon_person
import modus.composeapp.generated.resources.icon_short_arrow_right
import modus.composeapp.generated.resources.payments
import modus.composeapp.generated.resources.week
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class PaymentsDetailAdminScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { PaymentsAdminViewModel() }

        navigatorContent(viewModel, navigator)
        paymentsDetailAdminContent(viewModel)
    }
}

@Composable
private fun navigatorContent(viewModel: PaymentsAdminViewModel, navigator: Navigator) {
    val navigateToPaymentsAdmin = viewModel.navigateToPaymentsAdminFromPaymentsDetail.collectAsState().value
    val navigateToPaymentsWeekDetail = viewModel.navigateToReviewDetailFromPaymentsDetail.collectAsState().value

    LaunchedEffect(navigateToPaymentsAdmin) {
        if (navigateToPaymentsAdmin) {
            navigator.push(PaymentsAdminScreen())
            viewModel.resetNavigation()
        }
    }

    LaunchedEffect(navigateToPaymentsWeekDetail) {
        if (navigateToPaymentsWeekDetail) {
            navigator.push(PaymentsReviewDetailAdminScreen())
            viewModel.resetNavigation()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun paymentsDetailAdminContent(viewModel: PaymentsAdminViewModel) {
    val isScreenPaymentsDetailLoading = viewModel.isScreenPaymentsDetailLoading.collectAsState().value
    val isScreenPaymentsDetailRefreshing = viewModel.isScreenPaymentsDetailRefreshing.collectAsState().value

    Column (
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
            PullToRefreshBox (isRefreshing = isScreenPaymentsDetailRefreshing, onRefresh = { viewModel.refreshScreenPaymentsDetail() }) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    InformationPendingSection(viewModel)
                    SearchFieldSection(viewModel)
                }
            }
            ListOfReviewsSection(viewModel)
        }
    }
}

@Composable
private fun HeaderSection(viewModel: PaymentsAdminViewModel) {
    val week = viewModel.weekSelected.collectAsState().value

    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToPaymentsAdminFromPaymentsDetailScreen() }) {
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
            text = stringResource(Res.string.week) + " " + week,
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteBeigeColor
        )
    }
}

@Composable
private fun InformationPendingSection(viewModel: PaymentsAdminViewModel) {
    val numberOfPendingReviews = viewModel.selectedNumberOfPendingReviews.collectAsState().value

    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp, top = 20.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.size(150.dp),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteBeige2Color)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if ((numberOfPendingReviews.toIntOrNull() ?: 0) > 0) {
                        Text(
                            text = numberOfPendingReviews,
                            fontSize = 44.sp,
                            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                            color = WhiteColor
                        )
                    }
                    Text(
                        text = when {
                            numberOfPendingReviews.toIntOrNull() == 0 -> "No Pending\nReviews"
                            numberOfPendingReviews.toIntOrNull() == 1 -> "Pending\nReview"
                            else -> "Pending\nReviews"
                        },
                        textAlign = TextAlign.Center,
                        fontSize = if ((numberOfPendingReviews.toIntOrNull() ?: 0) > 0) 14.sp else 20.sp,
                        lineHeight = if ((numberOfPendingReviews.toIntOrNull() ?: 0) > 0) 13.sp else 19.sp,
                        fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                        color = WhiteColor
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchFieldSection(viewModel: PaymentsAdminViewModel) {
    val documentFilter = viewModel.documentFilter.collectAsState().value

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 15.dp),
        shape = RoundedCornerShape(8.dp),
        value = documentFilter,
        onValueChange = { newValue ->
            val filteredValue = newValue.filter { it.isDigit() }
            viewModel.updateDocumentFilter(filteredValue)
        },
        label = { Text(text = "Search by Document", fontFamily = FontFamily(Font(Res.font.Inter_Bold))) },
        singleLine = true,
        leadingIcon = {
            Icon(modifier = Modifier.size(24.dp), painter = painterResource(Res.drawable.icon_person), contentDescription = "Person Icon")
        },
        trailingIcon = {
            if (documentFilter.isNotEmpty()) {
                IconButton(onClick = { viewModel.updateDocumentFilter("") }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear text")
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BlackColor,
            focusedLabelColor = BlackColor,
            focusedLeadingIconColor = BlackColor,
            unfocusedLeadingIconColor = BlackColor.copy(alpha = alpha_0_7),
            disabledLeadingIconColor = BlackColor.copy(alpha = alpha_0_4),
            errorLeadingIconColor = ErrorColor,
            errorTrailingIconColor = BlackColor
        ),
        textStyle = TextStyle (
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
        )
    )
}

@Composable
private fun ListOfReviewsSection(viewModel: PaymentsAdminViewModel) {
    val weekSelected = viewModel.weekSelected.collectAsState().value
    val reviewsMap = viewModel.reviews.collectAsState().value
    val documentFilter = viewModel.documentFilter.collectAsState().value

    val reviewsFilter = reviewsMap.mapValues { (_, reviews) ->
        if (documentFilter.isBlank()) {
            reviews.sortedByDescending { it.number }
        } else {
            reviews.filter { it.userDocument.contains(documentFilter, ignoreCase = true) }
                .sortedByDescending { it.number }
        }
    } as HashMap<String, List<ReviewRepository.Review>>

    val reviews: List<ReviewRepository.Review> = reviewsFilter[weekSelected] ?: emptyList()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 15.dp).padding(bottom = 15.dp, top = 5.dp)) {
        items(reviews.sortedByDescending { it.state.toIntOrNull() ?: 0 }) { review ->
            ReviewItem(
                name = review.user?.name ?: "User Name Not Found",
                document = review.userDocument,
                number = review.number,
                state = review.state,
                onReviewClick = { viewModel.navigateToReviewDetailFromPaymentsDetailScreen(review) }
            )
        }
    }
}

@Composable
private fun ReviewItem(name: String, document: String, number: String, state: String, onReviewClick: () -> Unit) {

    val backgroundColor = when (state) {
        "1" -> GreenDarkColor
        "2" -> ErrorColor
        else -> WhiteColor
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).height(70.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
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
                    color = if (state == "3") BlackColor else WhiteColor
                )
                Text(
                    text = "$document - $number",
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = if (state == "3") BlackLightColor else Color(color = 0xFFfaf7f5)
                )
            }

            Card(
                modifier = Modifier.fillMaxHeight().clickable { onReviewClick() },
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