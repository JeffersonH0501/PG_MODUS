package com.uniandes.modus.ui.games.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.uniandes.modus.BlackLightColor
import com.uniandes.modus.GrayColor
import com.uniandes.modus.GrayLightColor
import com.uniandes.modus.getConnectivityChecker
import com.uniandes.modus.getNowLocalDateTime
import com.uniandes.modus.getSundayOfWeek
import com.uniandes.modus.getTodayLocalDate
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.icon_coffee
import modus.composeapp.generated.resources.icon_no_internet
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun StreamSection(viewModel: GamesUserViewModel) {
    val game = viewModel.gameSelected.collectAsState().value
    val isCheckingConnectionScreenDetailGames = viewModel.isCheckingConnectionScreenDetailGames.collectAsState().value

    game?.let {
        val context = LocalContext.current

        val year = game.year
        val week = game.week
        val streamId = game.streamId.toString()

        val sundayStreaming = getSundayOfWeek(year.toInt(), week.toInt())
        val currentDate = getTodayLocalDate()
        val dateOfWeek = getSundayOfWeek(year.toInt(), week.toInt())
        val now = getNowLocalDateTime()

        val state = when {
            dateOfWeek < currentDate -> "The Game Has Ended On ${sundayStreaming.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${sundayStreaming.dayOfMonth}, ${sundayStreaming.year}"
            dateOfWeek == currentDate -> {
                val currentHour = now.hour
                when {
                    currentHour < 21 -> "The Game Is Coming Soon Today At 9:00 pm"
                    currentHour in 21..21 -> "In Live"
                    else -> "The Game Has Ended On ${sundayStreaming.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${sundayStreaming.dayOfMonth}, ${sundayStreaming.year}"
                }
            }
            else -> {
                "The Game Will Be On ${sundayStreaming.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${sundayStreaming.dayOfMonth}, ${sundayStreaming.year}"
            }
        }

        if (streamId != "null") {
            if (getConnectivityChecker().isInternetAvailable() && isCheckingConnectionScreenDetailGames) {
                AndroidView(
                    modifier = Modifier.fillMaxWidth().aspectRatio(ratio = 16f / 9f),
                    factory = {
                        YouTubePlayerView(context).apply {
                            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    youTubePlayer.loadVideo(videoId = streamId, startSeconds = 0f)
                                }
                            })
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ratio = 16f / 9f)
                        .background(color = GrayLightColor)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            modifier = Modifier.size(48.dp),
                            painter = painterResource(Res.drawable.icon_no_internet),
                            contentDescription = "Coffee Icon",
                            tint = GrayColor
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No internet connection",
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                            color = GrayColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 16f / 9f)
                    .background(color = GrayLightColor)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        painter = painterResource(Res.drawable.icon_coffee),
                        contentDescription = "Coffee Icon",
                        tint = GrayColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = state,
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                        color = GrayColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}