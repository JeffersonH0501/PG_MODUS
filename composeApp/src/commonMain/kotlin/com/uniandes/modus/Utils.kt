package com.uniandes.modus

import androidx.compose.runtime.Composable
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.good_afternoon
import modus.composeapp.generated.resources.good_morning
import modus.composeapp.generated.resources.good_night
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.uniandes.modus.cache.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val department: String,
    val city: String
)

//******************************** COLORS ******************************************************************************************************************************

const val alpha_0_7 = 0.6f
const val alpha_0_4 = 0.4f

val WinnerGoldColor = Color(0xFFe2c446)

val GreenColor = Color(color = 0xFF00BF63)
val GreenDarkColor = Color(color = 0xFF1E8C00)
val RedColor = Color(color = 0xFFFF0013)

val OrangeColor = Color(color = 0xFFFF8800)
val BlueColor = Color(color = 0xFF007CD6)

val GrayColor = Color(color = 0xFF7F8C8D)
val GrayLightColor = Color(color = 0xFFEDEDED)

val BlackColor = Color(color = 0xFF000000)
val BlackLightColor = Color(color = 0xFF000000).copy(alpha = alpha_0_7)

val WhiteColor = Color(color = 0xFFFFFFFF)
val WhiteBeigeColor = Color(color = 0xFFC3B7AB)
val WhiteBeige2Color = Color(color = 0xFFab9987)

val ErrorColor = Color(color = 0xFFBA1A1A)

val brushBlackBackGround = Brush.linearGradient(colors = listOf(Color(color = 0xFF333333), Color(color = 0xFF1A1A1A)), start = Offset.Zero, end = Offset.Infinite)
val brushWhiteBackGround = Brush.linearGradient(colors = listOf(Color(color = 0xFFfaf7f5), Color(color = 0xFFF2EDE8)), start = Offset.Zero, end = Offset.Infinite)

fun parseHexColor(hex: String): Color {
    val cleanedHex = hex.removePrefix("#")
    val colorInt = cleanedHex.toLong(16).toInt()
    return Color((0xFF shl 24) or colorInt)
}

//******************************** DATE FUNCTIONS **********************************************************************************************************************

fun getNowInstant(): Instant {
    return Clock.System.now()
}

fun getNowLocalDateTime(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

fun getCurrentYear(): Int {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
}

fun getCurrentWeek(): Int {
    val today = Clock.System.now().toLocalDateTime(TimeZone.of("America/Bogota")).date
    val nextSunday = today.plus(DatePeriod(days = 7 - today.dayOfWeek.ordinal))
    return (nextSunday.dayOfYear - 1) / 7 + 1
}

fun getTotalWeeksInYear(year: Int): Int {
    val lastDayOfYear = LocalDate(year, 12, 31)
    val weekNumber = lastDayOfYear.getISOWeekNumber()
    return if (weekNumber == 1) 52 else weekNumber
}

fun getSundayOfWeek(year: Int, week: Int): LocalDate {
    val firstDayOfYear = LocalDate(year, 1, 1)

    val firstDayOfWeek = firstDayOfYear.dayOfWeek.ordinal

    val firstSunday = if (firstDayOfWeek == 6) firstDayOfYear
    else firstDayOfYear.plus(DatePeriod(days = 6 - firstDayOfWeek))

    return firstSunday.plus(DatePeriod(days = (week - 1) * 7))
}

fun getTodayLocalDate(): LocalDate {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}

@Composable
fun getTimeOfDay(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = now.hour

    return when (hour) {
        in 5..11 -> stringResource(Res.string.good_morning)
        in 12..17 -> stringResource(Res.string.good_afternoon)
        else -> stringResource(Res.string.good_night)
    }
}

fun LocalDate.getISOWeekNumber(): Int {
    val dayOfYear = dayOfYear
    val dayOfWeek = dayOfWeek.isoDayNumber

    val tempWeek = (dayOfYear - dayOfWeek + 10) / 7
    return if (tempWeek < 1) {
        LocalDate(year - 1, 12, 31).getISOWeekNumber()
    } else if (tempWeek > 52 && !isDateInWeekOneOfNextYear()) {
        53
    } else if (tempWeek > 52) {
        1
    } else {
        tempWeek
    }
}

fun LocalDate.isDateInWeekOneOfNextYear(): Boolean {
    val nextYearDate = LocalDate(year + 1, 1, 4)
    return nextYearDate.minus(DatePeriod(days = 7 * (nextYearDate.getISOWeekNumber() - 1))).dayOfYear <= dayOfYear
}

//******************************** EVENTUAL CONNECTIVITY FUNCTIONS **********************************************************************************************************************

interface ConnectivityChecker {
    fun isInternetAvailable(): Boolean
    fun observeConnectivity(): Flow<Boolean>
}

expect fun getConnectivityChecker(): ConnectivityChecker

expect fun getCacheDataBase(): AppDatabase?