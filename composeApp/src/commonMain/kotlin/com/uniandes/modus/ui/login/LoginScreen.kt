package com.uniandes.modus.ui.login

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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import com.uniandes.modus.BlackColor
import com.uniandes.modus.BlackLightColor
import com.uniandes.modus.ErrorColor
import com.uniandes.modus.WhiteBeige2Color
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.alpha_0_4
import com.uniandes.modus.alpha_0_7
import com.uniandes.modus.brushWhiteBackGround
import com.uniandes.modus.ui.home.HomeScreen
import com.uniandes.modus.ui.register.ContactRegisterScreen
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Inter_ExtraBold
import modus.composeapp.generated.resources.Inter_Medium
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.app_icon
import modus.composeapp.generated.resources.app_icon_description
import modus.composeapp.generated.resources.app_name
import modus.composeapp.generated.resources.create_account
import modus.composeapp.generated.resources.document_login
import modus.composeapp.generated.resources.enter
import modus.composeapp.generated.resources.forgot_your_password
import modus.composeapp.generated.resources.icon_enter
import modus.composeapp.generated.resources.icon_lock
import modus.composeapp.generated.resources.icon_person
import modus.composeapp.generated.resources.icon_warning
import modus.composeapp.generated.resources.password
import modus.composeapp.generated.resources.recover
import org.jetbrains.compose.resources.Font

class LoginScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { LoginViewModel() }

        navigationContent(viewModel, navigator)
        loginContent(viewModel)
    }

}

@Composable
private fun navigationContent(viewModel: LoginViewModel, navigator: Navigator) {
    val navigationEvent = viewModel.navigationEvent.collectAsState().value

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is LoginViewModel.LoginNavigationEvent.NavigateToHome -> {
                navigator.replaceAll(HomeScreen())
                viewModel.resetNavigation()
            }
            is LoginViewModel.LoginNavigationEvent.NavigateToRegister -> {
                navigator.push(ContactRegisterScreen())
                viewModel.resetNavigation()
            }
            else -> Unit
        }
    }
}

@Composable
private fun loginContent(viewModel: LoginViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().background(brush = brushWhiteBackGround),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        appLogo()
        loginTextFields(viewModel)
        recoverPassword()
        errorMessage(viewModel)
        enterButton(viewModel)
        createAccount(viewModel)
    }
}

@Composable
private fun appLogo() {
    Icon(
        painter = painterResource(Res.drawable.app_icon),
        contentDescription = stringResource(Res.string.app_icon_description),
        modifier = Modifier.size(130.dp),
        tint = WhiteBeige2Color
    )
    Text(
        modifier = Modifier.padding(bottom = 40.dp),
        text = stringResource(Res.string.app_name),
        fontSize = 34.sp,
        fontFamily = FontFamily(Font(Res.font.Inter_ExtraBold)),
        color = WhiteBeige2Color,
        lineHeight = 34.sp,
    )
}

@Composable
private fun loginTextFields(viewModel: LoginViewModel) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 15.dp),
        shape = RoundedCornerShape(8.dp),
        value = viewModel.document.value,
        onValueChange = { newValue ->
            val filteredValue = newValue.filter { it.isDigit() }
            viewModel.updateDocument(newText = filteredValue)
        },
        label = { Text(text = stringResource(Res.string.document_login), fontFamily = FontFamily(Font(Res.font.Inter_Bold))) },
        isError = viewModel.documentError.value,
        singleLine = true,
        leadingIcon = {
            Icon(modifier = Modifier.size(24.dp), painter = painterResource(Res.drawable.icon_person), contentDescription = "Person Icon")
        },
        trailingIcon = {
            if (viewModel.document.value.isNotEmpty()) {
                IconButton(onClick = { viewModel.updateDocument(newText = "") }) {
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
        label = { Text(text = stringResource(Res.string.password), fontFamily = FontFamily(Font(Res.font.Inter_Bold))) },
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
}

@Composable
private fun recoverPassword() {
    Row(modifier = Modifier.padding(top = 10.dp, bottom = 30.dp)) {
        Text(
            text = stringResource(Res.string.forgot_your_password),
            fontSize = 16.sp,
            lineHeight = 16.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = BlackLightColor
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(Res.string.recover),
            fontSize = 16.sp,
            lineHeight = 16.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = BlackColor
        )
    }
}

@Composable
private fun errorMessage(viewModel: LoginViewModel) {
    if (viewModel.messageError.value.isNotEmpty()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(Res.drawable.icon_warning),
                contentDescription = "Warning Icon",
                tint = ErrorColor
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = viewModel.messageError.value,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                color = ErrorColor
            )
        }
    }
}

@Composable
private fun enterButton(viewModel: LoginViewModel) {
    val isLoading = viewModel.isLoading.collectAsState().value

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .padding(top = 30.dp, bottom = 10.dp)
            .height(60.dp)
            .clickable(onClick = { if(!isLoading) viewModel.validateLogin() }),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = BlackColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
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
                        text = stringResource(Res.string.enter),
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                        color = WhiteColor
                    )
                }
            }
        }
    }
}

@Composable
private fun createAccount(viewModel: LoginViewModel) {
    Text(
        modifier = Modifier.clickable { viewModel.navigateToRegisterScreen() },
        text = stringResource(Res.string.create_account),
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
        color = BlackColor
    )
}