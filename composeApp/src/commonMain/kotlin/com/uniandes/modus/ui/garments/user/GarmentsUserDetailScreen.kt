package com.uniandes.modus.ui.garments.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
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
import com.uniandes.modus.WhiteBeige2Color
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.brushBlackBackGround
import com.uniandes.modus.brushWhiteBackGround
import com.uniandes.modus.parseHexColor
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Inter_Medium
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.arrow_back
import modus.composeapp.generated.resources.icon_not_save
import modus.composeapp.generated.resources.icon_save
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class GarmentsUserDetailScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { GarmentsUserViewModel() }

        navigatorContent(viewModel, navigator)
        garmentsUserDetailContent(viewModel)
    }

}

@Composable
private fun navigatorContent(viewModel: GarmentsUserViewModel, navigator: Navigator) {
    val navigationEvent = viewModel.navigationEvent.collectAsState().value

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is GarmentsUserViewModel.GarmentsNavigationEvent.NavigateToBackFromGarmentsDetail -> {
                viewModel.resetNavigation()
                navigator.pop()
            }
            else -> Unit
        }
    }
}

@Composable
private fun garmentsUserDetailContent(viewModel: GarmentsUserViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().background(brush = brushWhiteBackGround),
        verticalArrangement = Arrangement.Top
    ) {
        HeaderSection(viewModel)
        GarmentsDetail(viewModel)
    }
}

@Composable
private fun HeaderSection(viewModel: GarmentsUserViewModel) {
    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToBackScreenFromGarmentsDetailScreen() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.arrow_back),
                tint = WhiteColor
            )
        }
        Text(
            text = "Garment Detail",
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteColor
        )
    }
}

@Composable
private fun GarmentsDetail(viewModel: GarmentsUserViewModel) {
    val garment = viewModel.garmentSelected.collectAsState().value
    val savedGarments = viewModel.savedGarments.collectAsState().value

    garment?.let {

        garment.images.values.flatten().forEach { imageUrl ->
            asyncPainterResource(data = imageUrl)
        }

        var selectedColor by remember { mutableStateOf(garment.images.keys.firstOrNull() ?: "") }
        val images = garment.images[selectedColor] ?: emptyList()

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Text(
                text = garment.name,
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                color = BlackColor,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))
            ImageCarousel(images = images)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = BlackColor)) {
                                append("Reference: ")
                            }
                            withStyle(style = SpanStyle(color = BlackLightColor)) {
                                append(garment.reference)
                            }
                        },
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = BlackColor)) {
                                append("Sizes: ")
                            }
                            withStyle(style = SpanStyle(color = BlackLightColor)) {
                                append(garment.sizes.joinToString(", "))
                            }
                        },
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Colors:",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                        color = BlackColor,
                        lineHeight = 16.sp
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        garment.images.keys.forEach { hex ->
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(hex))
                                    .border(
                                        width = if (hex == selectedColor) 2.dp else 1.dp,
                                        color = if (hex == selectedColor) BlackColor else BlackLightColor,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColor = hex }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                if (savedGarments.contains(garment)) {
                    Icon(
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                viewModel.notSaveGarment(garment)
                            },
                        painter = painterResource(Res.drawable.icon_save),
                        contentDescription = stringResource(Res.string.arrow_back),
                        tint = BlackColor
                    )
                } else {
                    Icon(
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                viewModel.saveGarment(garment)
                            },
                        painter = painterResource(Res.drawable.icon_not_save),
                        contentDescription = stringResource(Res.string.arrow_back),
                        tint = BlackColor
                    )
                }
            }
        }
    }
}

@Composable
fun ImageCarousel(images: List<String>) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { images.size }
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
                .clip(RoundedCornerShape(8.dp)),
        ) { page ->
            KamelImage(
                modifier = Modifier.fillMaxSize(),
                resource = { asyncPainterResource(data = images[page]) },
                onLoading = {
                    CircularProgressIndicator(
                        color = WhiteColor,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(40.dp).padding(5.dp)
                    )
                },
                contentDescription = "Garment Image $page"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.Center) {
            repeat(images.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) BlackColor else BlackLightColor)
                )
            }
        }
    }
}
