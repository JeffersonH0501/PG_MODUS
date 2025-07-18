package com.uniandes.modus.ui.garments.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.uniandes.modus.WhiteBeigeColor
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.brushBlackBackGround
import com.uniandes.modus.brushWhiteBackGround
import com.uniandes.modus.parseHexColor
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Inter_Medium
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.arrow_back
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource

class GarmentsUserCategoryScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { GarmentsUserViewModel() }

        navigatorContent(viewModel, navigator)
        garmentsUserCategoryContent(viewModel)
    }
}

@Composable
private fun navigatorContent(viewModel: GarmentsUserViewModel, navigator: Navigator) {
    val navigationEvent = viewModel.navigationEvent.collectAsState().value

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {

            is GarmentsUserViewModel.GarmentsNavigationEvent.NavigateToGarmentsFromGarmentsCategory -> {
                viewModel.resetNavigation()
                navigator.pop()
            }

            is GarmentsUserViewModel.GarmentsNavigationEvent.NavigateToGarmentsDetailFromGarmentsCategory -> {
                viewModel.resetNavigation()
                navigator.push(GarmentsUserDetailScreen())
            }

            else -> Unit
        }
    }
}

@Composable
private fun garmentsUserCategoryContent(viewModel: GarmentsUserViewModel) {
    Column (
        modifier = Modifier.fillMaxSize().background(brush = brushWhiteBackGround),
        verticalArrangement = Arrangement.Top
    ) {
        HeaderSection(viewModel)
        GarmentsList(viewModel)
    }
}

@Composable
private fun HeaderSection(viewModel: GarmentsUserViewModel) {
    val category = viewModel.categorySelected.collectAsState().value

    val categoryString = when (category) {
        "0" -> "Saved"
        "1" -> "Men"
        "2" -> "Women"
        "3" -> "Boys"
        "4" -> "Girls"
        else -> "Undefined"
    }

    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToGarmentsScreenFromGarmentsCategoryScreen() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.arrow_back),
                tint = WhiteColor
            )
        }
        Text(
            text = "Garments - ",
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteColor
        )
        Text(
            text = categoryString,
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteBeigeColor
        )
    }
}

@Composable
private fun GarmentsList(viewModel: GarmentsUserViewModel) {
    val garments = when(val categorySelect = viewModel.categorySelected.collectAsState().value) {
        "0" -> viewModel.savedGarments.collectAsState().value
        else -> viewModel.garments.collectAsState().value.filter { it.category == categorySelect }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(garments.size) { index ->
            garmentItem(
                urlImage = garments[index].images.entries.firstOrNull()?.value?.getOrNull(0) ?: "",
                name = garments[index].name,
                sizes = garments[index].sizes,
                colors = garments[index].images.keys,
                onClick = {
                    viewModel.setGarmentSelected(garments[index])
                    viewModel.navigateToGarmentsDetailScreenFromGarmentsCategoryScreen()
                }
            )
        }
    }
}

@Composable
private fun garmentItem(urlImage: String, name: String, sizes: List<String>, colors: Set<String>, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(4f / 5f)
                .clickable { onClick() },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteColor),
        ) {
            KamelImage(
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                resource = { asyncPainterResource(data = urlImage) },
                onLoading = { CircularProgressIndicator(color = WhiteColor, strokeWidth = 2.dp, modifier = Modifier.size(40.dp).padding(5.dp)) },
                contentDescription = "Garment Image"
            )
        }
        Column {
            Text(
                modifier = Modifier.clickable { onClick() },
                text = name,
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                color = BlackColor,
                lineHeight = 18.sp
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = BlackColor)) {
                        append("Sizes: ")
                    }
                    withStyle(style = SpanStyle(color = BlackLightColor)) {
                        append(sizes.joinToString(", "))
                    }
                },
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                lineHeight = 14.sp
            )
            Text(
                text = "Colors:",
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                color = BlackColor,
                lineHeight = 14.sp
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                colors.forEach { hex ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(parseHexColor(hex))
                            .border(1.dp, BlackLightColor, CircleShape)
                    )
                }
            }
        }
    }
}