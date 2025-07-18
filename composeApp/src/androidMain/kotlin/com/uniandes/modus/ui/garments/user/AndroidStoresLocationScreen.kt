package com.uniandes.modus.ui.garments.user

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.uniandes.modus.BlackColor
import com.uniandes.modus.BlackLightColor
import com.uniandes.modus.WhiteBeige2Color
import com.uniandes.modus.WhiteColor
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
actual fun MapSection(viewModel: GarmentsUserViewModel) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { granted ->
        hasLocationPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(hasLocationPermission) {
        println(hasLocationPermission)
        if (hasLocationPermission) {
            val location = getCurrentLocation(context)
            if (location != null) {
                userLocation = location
            }
        }
    }

    val bounds = remember {
        LatLngBounds(
            LatLng(7.8700, -72.5300),
            LatLng(7.9200, -72.4700)
        )
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val currentLatLng = cameraPositionState.position.target
            if (!bounds.contains(currentLatLng)) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngBounds(bounds, 100)
                )
            }
        }
    }

    val points = listOf(
        LatLng(7.883056, -72.50225),
        LatLng(7.910611, -72.519583),
        LatLng(7.911694, -72.473194)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                points.forEachIndexed { idx, latLng ->
                    val icon = remember {
                        bitmapDescriptorFromText("${idx + 1}")
                    }

                    Marker(
                        state = MarkerState(position = latLng),
                        title = "Point ${idx + 1}",
                        snippet = "${latLng.latitude}, ${latLng.longitude}",
                        icon = icon
                    )
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(20.dp)) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val distances = points.map { point ->
                    point to distanceInKm(userLocation, point)
                }
                item {
                    CardItem(
                        number = "1",
                        name = "Modus headquarters Malecon",
                        address = "Street 9 # 10E-11",
                        distance = if (distances[0].second == -1.0f) {
                            "UNK"
                        } else {
                            String.format(
                                Locale.getDefault(),
                                "%.0f KM",
                                distances[0].second
                            )
                        }
                    )
                }
                item {
                    CardItem(
                        number = "2",
                        name = "Modus headquarters Zulia",
                        address = "Street 9 # 10E-11",
                        distance = if (distances[0].second == -1.0f) {
                            "UNK"
                        } else {
                            String.format(
                                Locale.getDefault(),
                                "%.0f KM",
                                distances[1].second
                            )
                        }
                    )
                }
                item {
                    CardItem(
                        number = "3",
                        name = "Modus headquarters La Libertidad",
                        address = "Street 9 # 10E-11",
                        distance = if (distances[0].second == -1.0f) {
                            "UNK"
                        } else {
                            String.format(
                                Locale.getDefault(),
                                "%.0f KM",
                                distances[2].second
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CardItem(number: String, name: String, address: String, distance: String) {

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).height(70.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteColor)
    ) {
        Row (
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 10.dp)
                    .clip(shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, topEnd = 0.dp, bottomEnd = 0.dp))
                    .background(color = WhiteBeige2Color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = number,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = WhiteColor,
                    lineHeight = 16.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = BlackColor,
                    lineHeight = 16.sp
                )
                Text(
                    text = address,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = BlackLightColor,
                    lineHeight = 16.sp
                )
            }

            if (distance != "UNK") {
                Card(
                    modifier = Modifier.fillMaxHeight(),
                    shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 8.dp, bottomEnd = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = WhiteColor,
                        contentColor = BlackColor
                    )
                ) {
                    Box(modifier = Modifier.fillMaxHeight().padding(horizontal = 15.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = distance,
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                            color = BlackLightColor,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

fun bitmapDescriptorFromText(text: String): BitmapDescriptor {
    val paint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    val width = 80
    val height = 80
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawCircle(width / 2f, height / 2f, 35f, Paint().apply { color = 0xFFab9987.toInt() })
    canvas.drawText(text, width / 2f, height / 2f + 15f, paint)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(context: Context): LatLng? = suspendCancellableCoroutine { cont ->
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 1000
    ).setMaxUpdates(1).build()

    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation
            if (location != null) {
                cont.resume(LatLng(location.latitude, location.longitude)) { cause, _, _ ->
                    null?.let {
                        it(
                            cause
                        )
                    }
                }
            } else {
                cont.resume(null) { cause, _, _ -> null?.let { it(cause) } }
            }
            fusedClient.removeLocationUpdates(this)
        }
    }

    fusedClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
    cont.invokeOnCancellation {
        fusedClient.removeLocationUpdates(callback)
    }
}

fun distanceInKm(from: LatLng?, to: LatLng): Float {
    from?.let {
        val results = FloatArray(1)
        Location.distanceBetween(to.latitude, to.longitude, from.latitude, from.longitude, results)
        return results[0] / 1000
    }
    return -1.0f
}