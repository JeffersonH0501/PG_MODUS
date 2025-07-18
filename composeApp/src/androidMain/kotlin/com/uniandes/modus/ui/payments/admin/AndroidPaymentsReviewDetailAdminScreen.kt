package com.uniandes.modus.ui.payments.admin

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.uniandes.modus.BlackColor
import com.uniandes.modus.BlackLightColor
import com.uniandes.modus.ErrorColor
import com.uniandes.modus.GrayColor
import com.uniandes.modus.GrayLightColor
import com.uniandes.modus.GreenDarkColor
import com.uniandes.modus.WhiteColor
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Inter_Medium
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.featured_image
import modus.composeapp.generated.resources.icon_approve
import modus.composeapp.generated.resources.icon_reject
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun VoucherAdminSection(viewModel: PaymentsAdminViewModel) {
    val reviewSelected = viewModel.reviewSelected.collectAsState().value

    var showDialogReject by remember { mutableStateOf(false) }
    var showDialogApprove by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 30.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        if (reviewSelected != null) {
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(9f / 16f),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = BlackColor)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = WhiteColor, strokeWidth = 2.dp, modifier = Modifier.size(40.dp).padding(5.dp))
                    Image(
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                        painter = rememberAsyncImagePainter(model = reviewSelected.voucher),
                        contentDescription = stringResource(Res.string.featured_image),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Buttons(
                state = reviewSelected.state,
                onRejectClick = { showDialogReject = true },
                onApproveClick = { showDialogApprove = true }
            )
        }
    }

    if (showDialogApprove) {
        ApproveDialog(
            onDismiss = { showDialogApprove = false },
            viewModel = viewModel
        )
    }

    if (showDialogReject) {
        RejectDialog(
            onDismiss = { showDialogReject = false },
            viewModel = viewModel
        )
    }
}

@Composable
private fun Buttons(state: String, onRejectClick: () -> Unit, onApproveClick: () -> Unit) {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 2f/1.5f)
                .clickable(enabled = state == "3") { onRejectClick() },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = if (state == "3") ErrorColor else GrayLightColor)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(Res.drawable.icon_reject),
                    contentDescription = "manage_search",
                    tint = if (state == "3") WhiteColor else GrayColor
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 2f/1.5f)
                .clickable(enabled = state == "3") { onApproveClick() },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = if (state == "3") GreenDarkColor else GrayLightColor)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(Res.drawable.icon_approve),
                    contentDescription = "manage_search",
                    tint = if (state == "3") WhiteColor else GrayColor
                )
            }
        }
    }
}

@Composable
fun ApproveDialog( viewModel: PaymentsAdminViewModel, onDismiss: () -> Unit) {
    AlertDialog(
        containerColor = Color(color = 0xFFfaf7f5),
        shape = RoundedCornerShape(8.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Are you sure to approve?",
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                color = BlackColor
            )
        },
        text = {
            Text(
                text = "Remember you can't change the state of voucher after approve it.",
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                color = BlackLightColor
            )
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.weight(1f).height(45.dp).clickable(onClick = {
                        onDismiss()
                        viewModel.approveVoucher()
                    }),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenDarkColor)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Approve",
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
            }
        }
    )
}

@Composable
fun RejectDialog(viewModel: PaymentsAdminViewModel, onDismiss: () -> Unit) {
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        containerColor = Color(color = 0xFFfaf7f5),
        shape = RoundedCornerShape(8.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Write the Reason For Reject",
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                color = BlackColor
            )
        },
        text = {
            Column {
                Text(
                    text = "Please provide a reason for reject. Remember you can't change the state of voucher after rejecting it.",
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                    color = BlackLightColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField (
                    modifier = Modifier.fillMaxWidth(),
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = {
                        Text(
                            text = "Enter reason",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                            color = BlackLightColor
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = GrayLightColor,
                        unfocusedContainerColor = GrayLightColor,
                        focusedIndicatorColor = BlackColor,
                        unfocusedIndicatorColor = BlackColor
                    ),
                    textStyle = TextStyle (
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    )
                )
            }
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.weight(1f).height(45.dp).clickable(enabled = reason.isNotEmpty(), onClick = {
                        onDismiss()
                        viewModel.rejectVoucher(reason)
                    }),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = if (reason.isEmpty()) GrayLightColor else ErrorColor )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Reject",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                            color = if (reason.isEmpty()) GrayColor else WhiteColor
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
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
            }
        }
    )
}