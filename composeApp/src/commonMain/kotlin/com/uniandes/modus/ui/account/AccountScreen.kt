package com.uniandes.modus.ui.account

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.uniandes.modus.BlackColor
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.brushBlackBackGround
import com.uniandes.modus.ui.home.HomeScreen
import com.uniandes.modus.ui.login.LoginScreen
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.icon_exit
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class AccountScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { AccountViewModel() }

        navigationContent(viewModel, navigator)
        AccountContent(viewModel)
    }

}

@Composable
private fun navigationContent(viewModel: AccountViewModel, navigator: Navigator) {
    val navigateToHome = viewModel.navigateToHomeFromAccount.collectAsState().value
    val navigateToLogin = viewModel.navigateToLoginFromAccount.collectAsState().value

    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {
            navigator.push(HomeScreen())
            viewModel.resetNavigation()
        }
    }

    LaunchedEffect(navigateToLogin) {
        if (navigateToLogin) {
            navigator.replaceAll(LoginScreen())
            viewModel.resetNavigation()
        }
    }
}

@Composable
private fun AccountContent(viewModel: AccountViewModel) {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection(viewModel)
        AccountInformationSection(viewModel)
        LogOutButtonSection(viewModel)
    }
}

@Composable
private fun HeaderSection(viewModel: AccountViewModel) {
    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToHomeScreen() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Arrow Back",
                tint = WhiteColor
            )
        }
        Text(
            text = "Account",
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
            color = WhiteColor
        )
    }
}

@Composable
private fun AccountInformationSection(viewModel: AccountViewModel) {

}

@Composable
private fun LogOutButtonSection(viewModel: AccountViewModel) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .padding(top = 15.dp)
            .height(60.dp)
            .clickable(onClick = { viewModel.logOut() }),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = BlackColor)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.icon_exit),
                    contentDescription = "Exit Icon",
                    tint = WhiteColor
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Log Out",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = WhiteColor
                )
            }
        }
    }
}