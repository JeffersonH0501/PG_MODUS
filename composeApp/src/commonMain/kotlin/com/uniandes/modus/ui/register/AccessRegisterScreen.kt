package com.uniandes.modus.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.model.rememberScreenModel
import com.uniandes.modus.BlackColor
import com.uniandes.modus.ErrorColor
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.alpha_0_4
import com.uniandes.modus.alpha_0_7
import com.uniandes.modus.brushBlackBackGround
import com.uniandes.modus.ui.home.HomeScreen
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Inter_Medium
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.icon_enter
import modus.composeapp.generated.resources.icon_lock
import modus.composeapp.generated.resources.icon_person
import modus.composeapp.generated.resources.icon_warning
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class AccessRegisterScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { RegisterViewModel() }

        navigationContent(viewModel, navigator)
        accessRegisterContent(viewModel)
    }

}

@Composable
private fun navigationContent(viewModel: RegisterViewModel, navigator: Navigator) {
    val navigateToContactRegister = viewModel.navigateToContactRegister.collectAsState().value
    val navigateToHome = viewModel.navigateToHome.collectAsState().value

    LaunchedEffect(navigateToContactRegister) {
        if (navigateToContactRegister) {
            viewModel.resetNavigation()
            navigator.pop()
        }
    }

    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {
            viewModel.resetNavigation()
            navigator.replaceAll(HomeScreen())
        }
    }
}

@Composable
private fun accessRegisterContent(viewModel: RegisterViewModel) {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection(viewModel)
        AccessTextFieldsSection(viewModel)
        errorMessageSection(viewModel)
        AccessRegisterButtonSection(viewModel)
    }
}

@Composable
private fun HeaderSection(viewModel: RegisterViewModel) {
    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToContactRegisterScreen() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Arrow Back",
                tint = WhiteColor
            )
        }
        Text(
            text = "Create Account",
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteColor
        )
    }
}

@Composable
private fun AccessTextFieldsSection(viewModel: RegisterViewModel) {
    Text(
        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
        text = "Access Information",
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
        color = BlackColor
    )

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 15.dp),
        shape = RoundedCornerShape(8.dp),
        value = viewModel.document.value,
        onValueChange = { newValue ->
            val filteredValue = newValue.filter { it.isDigit() }
            viewModel.updateDocument(filteredValue)
        },
        label = { Text(text = "Document", fontFamily = FontFamily(Font(Res.font.Inter_Bold))) },
        isError = viewModel.documentError.value,
        singleLine = true,
        leadingIcon = {
            Icon(modifier = Modifier.size(24.dp), painter = painterResource(Res.drawable.icon_person), contentDescription = "Person Icon")
        },
        trailingIcon = {
            if (viewModel.document.value.isNotEmpty()) {
                IconButton(onClick = { viewModel.updateDocument("") }) {
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

    Spacer(modifier = Modifier.height(5.dp))

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 15.dp),
        shape = RoundedCornerShape(8.dp),
        value = viewModel.password.value,
        onValueChange = { viewModel.updatePassword(it) },
        label = { Text(text = "Password", fontFamily = FontFamily(Font(Res.font.Inter_Bold))) },
        isError = viewModel.passwordError.value,
        singleLine = true,
        leadingIcon = {
            Icon(modifier = Modifier.size(21.dp), painter = painterResource(Res.drawable.icon_lock), contentDescription = "Lock Icon")
        },
        trailingIcon = {
            if (viewModel.password.value.isNotEmpty()) {
                IconButton(onClick = { viewModel.updatePassword("") }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear text")
                }
            }
        },
        visualTransformation = PasswordVisualTransformation(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BlackColor,
            focusedLabelColor = BlackColor,
            focusedLeadingIconColor = BlackColor,
            unfocusedLeadingIconColor = BlackColor.copy(alpha = alpha_0_7),
            disabledLeadingIconColor = BlackColor.copy(alpha = alpha_0_4),
            errorLeadingIconColor = ErrorColor
        ),
        textStyle = TextStyle (
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
        )
    )

    Spacer(modifier = Modifier.height(5.dp))

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 15.dp),
        shape = RoundedCornerShape(8.dp),
        value = viewModel.confirmPassword.value,
        onValueChange = { viewModel.updateConfirmPassword(it) },
        label = { Text(text = "Confirm Password", fontFamily = FontFamily(Font(Res.font.Inter_Bold))) },
        isError = viewModel.confirmPasswordError.value,
        singleLine = true,
        leadingIcon = {
            Icon(modifier = Modifier.size(21.dp), painter = painterResource(Res.drawable.icon_lock), contentDescription = "Lock Icon")
        },
        trailingIcon = {
            if (viewModel.confirmPassword.value.isNotEmpty()) {
                IconButton(onClick = { viewModel.updateConfirmPassword("") }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear text")
                }
            }
        },
        visualTransformation = PasswordVisualTransformation(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BlackColor,
            focusedLabelColor = BlackColor,
            focusedLeadingIconColor = BlackColor,
            unfocusedLeadingIconColor = BlackColor.copy(alpha = alpha_0_7),
            disabledLeadingIconColor = BlackColor.copy(alpha = alpha_0_4),
            errorLeadingIconColor = ErrorColor
        ),
        textStyle = TextStyle (
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
        )
    )
}

@Composable
private fun errorMessageSection(viewModel: RegisterViewModel) {
    if (viewModel.messageAccessError.value.isNotEmpty()) {
        Row(modifier = Modifier.padding(top = 30.dp, bottom = 15.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(Res.drawable.icon_warning),
                contentDescription = "Warning Icon",
                tint = ErrorColor
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = viewModel.messageAccessError.value,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                color = ErrorColor
            )
        }
    }
}

@Composable
private fun AccessRegisterButtonSection(viewModel: RegisterViewModel) {
    val isLoading = viewModel.isLoading.collectAsState().value

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .padding(top = 15.dp)
            .height(60.dp)
            .clickable(onClick = { viewModel.validateRegister() }),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = BlackColor)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(Res.drawable.icon_enter),
                        contentDescription = "Enter Icon",
                        tint = WhiteColor
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Register",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                        color = WhiteColor
                    )
                }
            }

        }
    }
}