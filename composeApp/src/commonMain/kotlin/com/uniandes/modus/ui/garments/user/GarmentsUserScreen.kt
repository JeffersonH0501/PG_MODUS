package com.uniandes.modus.ui.garments.user

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Inter_Medium
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.arrow_back
import modus.composeapp.generated.resources.arrow_forward
import modus.composeapp.generated.resources.icon_no_internet
import modus.composeapp.generated.resources.icon_save
import modus.composeapp.generated.resources.icon_short_arrow_right
import modus.composeapp.generated.resources.icon_store_location
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class GarmentsUserScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { GarmentsUserViewModel() }

        navigatorContent(viewModel, navigator)
        garmentsUserContent(viewModel)
    }

}

@Composable
private fun navigatorContent(viewModel: GarmentsUserViewModel, navigator: Navigator) {
    val navigationEvent = viewModel.navigationEvent.collectAsState().value

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {

            is GarmentsUserViewModel.GarmentsNavigationEvent.NavigateToHomeFromGarments -> {
                viewModel.resetNavigation()
                navigator.pop()
            }

            is GarmentsUserViewModel.GarmentsNavigationEvent.NavigateToStoresLocationFromGarments -> {
                viewModel.resetNavigation()
                navigator.push(StoresLocationScreen())
            }

            is GarmentsUserViewModel.GarmentsNavigationEvent.NavigateToGarmentsCategoryFromGarments -> {
                viewModel.resetNavigation()
                navigator.push(GarmentsUserCategoryScreen())
            }

            is GarmentsUserViewModel.GarmentsNavigationEvent.NavigateToGarmentsDetailFromGarments -> {
                viewModel.resetNavigation()
                navigator.push(GarmentsUserDetailScreen())
            }

            else -> Unit
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun garmentsUserContent(viewModel: GarmentsUserViewModel) {
    val isScreenLoading = viewModel.isScreenGarmentsLoading.collectAsState().value
    val isScreenRefreshing = viewModel.isScreenGarmentsRefreshing.collectAsState().value
    val isDeviceWhitOutConnection = viewModel.isDeviceWhitOutConnectionScreenGarments.collectAsState().value

    Column (
        modifier = Modifier.fillMaxSize().background(brush = brushWhiteBackGround),
        verticalArrangement = Arrangement.Top
    ) {
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
        } else if (isScreenLoading) {
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
            PullToRefreshBox (isRefreshing = isScreenRefreshing, onRefresh = { viewModel.refreshGarments() }) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    CategoryList(viewModel, "1")
                    CategoryList(viewModel, "2")
                    CategoryList(viewModel, "3")
                    CategoryList(viewModel, "4")
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(viewModel: GarmentsUserViewModel) {
    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp, end = 10.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToHomeScreenFromGarmentsScreen() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.arrow_back),
                tint = WhiteColor
            )
        }
        Text(
            text = "Garments",
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteColor
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateToStoresLocationScreenFromGarmentsScreen() }) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(Res.drawable.icon_store_location),
                    contentDescription = stringResource(Res.string.arrow_back),
                    tint = WhiteBeigeColor,
                )
            }
            IconButton(onClick = {
                viewModel.setCategorySelected("0")
                viewModel.navigateToGarmentsCategoryScreenFromGarmentsScreen()
            }) {
                Icon(
                    modifier = Modifier.size(21.dp),
                    painter = painterResource(Res.drawable.icon_save),
                    contentDescription = stringResource(Res.string.arrow_back),
                    tint = WhiteBeigeColor
                )
            }
        }
    }
}

@Composable
private fun CategoryList(viewModel: GarmentsUserViewModel, category: String) {
    val garments = viewModel.garments.collectAsState().value.filter { it.category == category }

    val categoryString = when (category) {
        "0" -> "Most popular"
        "1" -> "Men"
        "2" -> "Women"
        "3" -> "Boys"
        "4" -> "Girls"
        else -> "Undefined"
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 15.dp, top = 10.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = categoryString,
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = BlackColor
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.clickable {
                viewModel.setCategorySelected(category)
                viewModel.navigateToGarmentsCategoryScreenFromGarmentsScreen()
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "View all",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                color = BlackColor
            )
            Icon(
                modifier = Modifier.size(30.dp),
                painter = painterResource(Res.drawable.icon_short_arrow_right),
                contentDescription = stringResource(Res.string.arrow_forward)
            )
        }
    }

    val itemWidthFraction = 0.4f

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(garments.size) { index ->
            Box(modifier = Modifier.fillParentMaxWidth(itemWidthFraction).padding(end = 10.dp)) {
                garmentItem(
                    urlImage = garments[index].images.entries.firstOrNull()?.value?.getOrNull(0) ?: "",
                    name = garments[index].name,
                    onClick = {
                        viewModel.setGarmentSelected(garments[index])
                        viewModel.navigateToGarmentsDetailScreenFromGarmentsScreen()
                    }
                )
            }
        }
    }

}

@Composable
private fun garmentItem(urlImage: String, name: String, onClick: () -> Unit) {
    Column {
        Card(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(4f / 5f)
                .clickable { onClick() },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteColor)
        ) {
            KamelImage(
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                resource = { asyncPainterResource(data = urlImage) },
                onLoading = { CircularProgressIndicator(color = WhiteColor, strokeWidth = 2.dp, modifier = Modifier.size(40.dp).padding(5.dp)) },
                contentDescription = "Garment Image"
            )
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            text = name,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
            color = BlackLightColor,
            lineHeight = 12.sp,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}