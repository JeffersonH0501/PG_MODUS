package com.uniandes.modus.ui.payments.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import java.io.File
import androidx.core.content.FileProvider
import com.uniandes.modus.BlackColor
import com.uniandes.modus.BlackLightColor
import com.uniandes.modus.BlueColor
import com.uniandes.modus.ErrorColor
import com.uniandes.modus.GrayColor
import com.uniandes.modus.GrayLightColor
import com.uniandes.modus.GreenDarkColor
import com.uniandes.modus.WhiteBeige2Color
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.getCurrentWeek
import com.uniandes.modus.getCurrentYear
import com.uniandes.modus.model.PaymentRepository
import com.uniandes.modus.model.ReviewRepository
import com.yalantis.ucrop.UCrop
import kotlinx.datetime.toJavaInstant
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Inter_Medium
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.and
import modus.composeapp.generated.resources.app_icon
import modus.composeapp.generated.resources.app_icon_description
import modus.composeapp.generated.resources.attach_icon
import modus.composeapp.generated.resources.camera
import modus.composeapp.generated.resources.choose_how_you_want_to_upload_it
import modus.composeapp.generated.resources.document
import modus.composeapp.generated.resources.enter_the_screenshot_or_photo_of_this_weeks_payments_receipt
import modus.composeapp.generated.resources.gallery
import modus.composeapp.generated.resources.icon_camera
import modus.composeapp.generated.resources.icon_delete
import modus.composeapp.generated.resources.icon_gallery
import modus.composeapp.generated.resources.icon_reload
import modus.composeapp.generated.resources.icon_search
import modus.composeapp.generated.resources.icon_send
import modus.composeapp.generated.resources.icon_upload_image
import modus.composeapp.generated.resources.number_of_the_week
import modus.composeapp.generated.resources.please_in_the_transaction_description_place_your
import modus.composeapp.generated.resources.select_voucher
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
actual fun VoucherUser(viewModel: PaymentsUserViewModel) {
    val paymentSelected = viewModel.paymentSelected.collectAsState().value
    val reviewSelected = viewModel.reviewSelected.collectAsState().value

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var croppedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val tempUriForCamera = remember { createTempImageFileUri(context) }
    val cropLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                UCrop.getOutput(intent)?.let { croppedUri ->
                    croppedImageUri = croppedUri
                    viewModel.selectVoucher(uriToByteArray(context, croppedUri))
                }
            }
        }
    }
    val launcherGallery = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            startImageCrop(context, it, cropLauncher)
        }
    }
    val launcherCamera = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            imageUri = tempUriForCamera
            startImageCrop(context, tempUriForCamera, cropLauncher)
        }
    }

    var showDialogSelectVoucher by remember { mutableStateOf(false) }
    var showDialogDetailReview by remember { mutableStateOf(false) }
    var showDialogDeleteReview by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 30.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        VoucherImageBox(
            selectedReview = reviewSelected,
            selectedPayment = paymentSelected,
            croppedImageUri = croppedImageUri,
            onClick = { showDialogSelectVoucher = true }
        )
        Spacer(modifier = Modifier.width(20.dp))
        VoucherButtonsBox(
            viewModel = viewModel,
            selectedReview = reviewSelected,
            croppedImageUri = croppedImageUri,
            onSearchClick = { showDialogDetailReview = true },
            onReloadClick = { showDialogSelectVoucher = true },
            onDeleteClick = { showDialogDeleteReview = true },
            onSendClick = { viewModel.uploadVoucher() }
        )
    }

    if (showDialogSelectVoucher && (reviewSelected == null || reviewSelected.state == "2") ) {
        VoucherUploadDialog(
            onDismiss = { showDialogSelectVoucher = false },
            onGalleryClick = { launcherGallery.launch("image/*") },
            onCameraClick = { launcherCamera.launch(tempUriForCamera) }
        )
    }

    if (showDialogDetailReview) {
        ReviewDetailDialog(
            onDismiss = { showDialogDetailReview = false },
            selectedReview = reviewSelected
        )
    }

    if (showDialogDeleteReview) {
        DeleteReviewDialog(
            onDismiss = { showDialogDeleteReview = false },
            onClickAccept = {
                viewModel.deleteReview()
                croppedImageUri = null
            }
        )
    }
}

@Composable
fun VoucherImageBox(selectedReview: ReviewRepository.Review?, selectedPayment: PaymentRepository.Payment?, croppedImageUri: Uri?, onClick: () -> Unit) {
    val painter = when {
        croppedImageUri != null -> rememberAsyncImagePainter(model = croppedImageUri)
        selectedReview != null -> rememberAsyncImagePainter(model = selectedReview.voucher)
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(9f / 16f),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = BlackColor)
    ) {
        if (painter != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = WhiteColor, strokeWidth = 2.dp, modifier = Modifier.size(40.dp).padding(5.dp))
                Image(
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                    painter = painter,
                    contentDescription = "Voucher Image",
                    contentScale = ContentScale.Crop
                )
            }
        } else if (selectedPayment!!.state == "2") {
            Column(
                modifier = Modifier.fillMaxSize().background(color = WhiteColor, RoundedCornerShape(8.dp)).clickable { onClick() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    painter = painterResource(Res.drawable.icon_upload_image),
                    contentDescription = stringResource(Res.string.attach_icon),
                    tint = BlackColor
                )
                Text(
                    text = "Upload Voucher",
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = BlackColor
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().background(color = WhiteColor, RoundedCornerShape(8.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier.size(85.dp),
                    painter = painterResource(Res.drawable.app_icon),
                    contentDescription = stringResource(Res.string.app_icon_description),
                    tint = WhiteBeige2Color
                )
                Text(
                    text = "MODUS",
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = WhiteBeige2Color
                )
            }
        }
    }
}

@Composable
fun VoucherButtonsBox(
    viewModel: PaymentsUserViewModel,
    selectedReview: ReviewRepository.Review?,
    croppedImageUri: Uri?,
    onSearchClick: () -> Unit,
    onReloadClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSendClick: () -> Unit
) {
    val isLoadingUploading = viewModel.isLoadingUploading.collectAsState().value
    val isLoadingDeleting = viewModel.isLoadingDeleting.collectAsState().value

    val year = viewModel.yearSelected.collectAsState().value
    val week = viewModel.weekSelected.collectAsState().value
    val isOldWeek = (year.toInt() < getCurrentYear() || (year.toInt() == getCurrentYear() && week.toInt() < getCurrentWeek()))

    var isSearchButtonEnable = false
    val isReloadButtonEnable: Boolean
    var isDeleteButtonEnable = false

    if (selectedReview != null) {
        isSearchButtonEnable = (selectedReview.state) == "1" || (selectedReview.state) == "2"
        isDeleteButtonEnable = (selectedReview.state) == "3"
        isReloadButtonEnable = (selectedReview.state) == "2" && !isOldWeek
    } else {
        isReloadButtonEnable = croppedImageUri != null
    }

    val isSendButtonEnable = croppedImageUri != null && !isDeleteButtonEnable


    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f/1.2f)
                .clickable(enabled = isSearchButtonEnable) { onSearchClick() },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = if (isSearchButtonEnable) BlueColor else GrayLightColor)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(Res.drawable.icon_search),
                    contentDescription = "manage_search",
                    tint = if (isSearchButtonEnable) WhiteColor else GrayColor
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f/1.2f)
                .clickable(enabled = isReloadButtonEnable) { onReloadClick() },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = if (isReloadButtonEnable) BlueColor else GrayLightColor)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(Res.drawable.icon_reload),
                    contentDescription = "restore_page",
                    tint = if (isReloadButtonEnable) WhiteColor else GrayColor
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f/1.2f)
                .clickable(enabled = isDeleteButtonEnable) { onDeleteClick() },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDeleteButtonEnable) ErrorColor else GrayLightColor)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (isLoadingDeleting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(21.dp),
                        painter = painterResource(Res.drawable.icon_delete),
                        contentDescription = "delete",
                        tint = if (isDeleteButtonEnable) WhiteColor else GrayColor
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f/1.2f)
                .clickable(enabled = isSendButtonEnable) { onSendClick() },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = if (isSendButtonEnable) GreenDarkColor else GrayLightColor)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (isLoadingUploading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(21.dp),
                        painter = painterResource(Res.drawable.icon_send),
                        contentDescription = "send",
                        tint = if (isSendButtonEnable) WhiteColor else GrayColor
                    )
                }
            }
        }
    }
}

@Composable
fun VoucherUploadDialog(onDismiss: () -> Unit, onGalleryClick: () -> Unit, onCameraClick: () -> Unit) {
    AlertDialog(
        containerColor = Color(color = 0xFFfaf7f5),
        shape = RoundedCornerShape(8.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(Res.string.select_voucher),
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                color = BlackColor
            )
        },
        text = {
            Column {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(Res.string.enter_the_screenshot_or_photo_of_this_weeks_payments_receipt))
                        append(stringResource(Res.string.please_in_the_transaction_description_place_your))
                        withStyle(style = SpanStyle(color = BlackColor, fontFamily = FontFamily(Font(Res.font.Inter_Bold)))) { append(stringResource(Res.string.document)) }
                        append(stringResource(Res.string.and))
                        withStyle(style = SpanStyle(color = BlackColor, fontFamily = FontFamily(Font(Res.font.Inter_Bold)))) { append(stringResource(Res.string.number_of_the_week)) }
                        append(".")
                    },
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                    color = BlackLightColor
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(Res.string.choose_how_you_want_to_upload_it),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                    color = BlackLightColor
                )
            }
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.weight(1f).height(45.dp).clickable(onClick = {
                        onDismiss()
                        onCameraClick()
                    }),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = BlackColor)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(Res.drawable.icon_camera),
                                contentDescription = "Camera Icon",
                                tint = WhiteColor
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(Res.string.camera),
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                                color = WhiteColor
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    modifier = Modifier.weight(1f).height(45.dp).clickable(onClick = {
                        onDismiss()
                        onGalleryClick()
                    }),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = BlackColor)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(Res.drawable.icon_gallery),
                                contentDescription = "Gallery Icon",
                                tint = WhiteColor
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(Res.string.gallery),
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                                color = WhiteColor
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun DeleteReviewDialog(onDismiss: () -> Unit, onClickAccept: () -> Unit) {
    AlertDialog(
        containerColor = Color(color = 0xFFfaf7f5),
        shape = RoundedCornerShape(8.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Are you sure you want to delete this review?",
                fontSize = 20.sp,
                lineHeight = 25.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                color = BlackColor
            )
        },
        text = {
            Column {
                Text(
                    text = "Please note that once you delete a review, you cannot recover it. Please check before continuing.",
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                    color = BlackLightColor
                )
            }
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.weight(1f).height(45.dp).clickable(onClick = {
                        onDismiss()
                    }),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = BlackColor)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                            color = WhiteColor
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    modifier = Modifier.weight(1f).height(45.dp).clickable(onClick = {
                        onDismiss()
                        onClickAccept()
                    }),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = ErrorColor)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Delete",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                            color = WhiteColor
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ReviewDetailDialog(onDismiss: () -> Unit, selectedReview: ReviewRepository.Review?) {
    AlertDialog(
        containerColor = Color(color = 0xFFfaf7f5),
        shape = RoundedCornerShape(8.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Review Number" + " " + selectedReview!!.number,
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                color = BlackColor
            )
        },
        text = {
            Column {
                Text(
                    text = "Description",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = BlackColor
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (selectedReview!!.description == null) "The review description will appear here when the administrator performs it."
                    else selectedReview.description!!,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                    color = BlackLightColor
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Date",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = BlackColor
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (selectedReview.dateReview == null) "The review date will appear here when the administrator performs it."
                    else ZonedDateTime
                        .ofInstant(selectedReview.dateReview.toJavaInstant(), ZoneId.of("GMT-5"))
                        .toLocalDate()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                    color = BlackLightColor
                )
            }
        },
        confirmButton = {
            Card(
                modifier = Modifier.fillMaxWidth(0.45f).height(45.dp).clickable(onClick = { onDismiss() }),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Accept",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                        color = WhiteColor
                    )
                }
            }
        }
    )
}

fun uriToByteArray(context: Context, uri: Uri): ByteArray {
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        return inputStream.readBytes()
    }
    throw IllegalArgumentException("Could not read file from Uri")
}

fun createTempImageFileUri(context: Context): Uri {
    val tempImageFile = File.createTempFile("voucher_", ".jpg", context.cacheDir).apply { deleteOnExit() }
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempImageFile)
}

fun startImageCrop(context: Context, sourceUri: Uri, cropLauncher: ActivityResultLauncher<Intent>) {
    context.cacheDir.listFiles()?.forEach { file ->
        if (file.name.startsWith("cropped_")) file.delete()
    }

    val destinationUri = Uri.fromFile(File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))

    val cropIntent = UCrop.of(sourceUri, destinationUri)
        .withAspectRatio(9f, 16f)
        .withMaxResultSize(1080, 1920)
        .getIntent(context)

    cropLauncher.launch(cropIntent)
}