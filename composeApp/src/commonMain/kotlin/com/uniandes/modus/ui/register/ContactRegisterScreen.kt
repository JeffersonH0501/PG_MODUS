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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import com.uniandes.modus.BlackColor
import com.uniandes.modus.ErrorColor
import com.uniandes.modus.WhiteColor
import com.uniandes.modus.alpha_0_4
import com.uniandes.modus.alpha_0_7
import com.uniandes.modus.brushBlackBackGround
import com.uniandes.modus.brushWhiteBackGround
import modus.composeapp.generated.resources.Inter_Bold
import modus.composeapp.generated.resources.Inter_Medium
import modus.composeapp.generated.resources.Res
import modus.composeapp.generated.resources.icon_city
import modus.composeapp.generated.resources.icon_department
import modus.composeapp.generated.resources.icon_email
import modus.composeapp.generated.resources.icon_enter
import modus.composeapp.generated.resources.icon_home
import modus.composeapp.generated.resources.icon_person
import modus.composeapp.generated.resources.icon_phone
import modus.composeapp.generated.resources.icon_warning
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class ContactRegisterScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { RegisterViewModel() }

        navigationContent(viewModel, navigator)
        registerContent(viewModel)
    }

}

@Composable
private fun navigationContent(viewModel: RegisterViewModel, navigator: Navigator) {
    val navigateToLogin = viewModel.navigateToLogin.collectAsState().value
    val navigateToAccessRegister = viewModel.navigateToAccessRegister.collectAsState().value

    LaunchedEffect(navigateToLogin) {
        if (navigateToLogin) {
            viewModel.resetNavigation()
            navigator.pop()
        }
    }

    LaunchedEffect(navigateToAccessRegister) {
        if (navigateToAccessRegister) {
            viewModel.resetNavigation()
            navigator.push(AccessRegisterScreen())
        }
    }
}

@Composable
fun registerContent(viewModel: RegisterViewModel) {
    Column (
        modifier = Modifier.fillMaxSize().background(brush = brushWhiteBackGround),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection(viewModel)
        ContactTextFieldsSection(viewModel)
        errorMessageSection(viewModel)
        ContactRegisterButtonSection(viewModel)
    }
}

@Composable
private fun HeaderSection(viewModel: RegisterViewModel) {
    Row (
        modifier = Modifier.fillMaxWidth().background(brush = brushBlackBackGround).padding(start = 10.dp).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.navigateToLoginScreen() }) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactTextFieldsSection(viewModel: RegisterViewModel) {

    Text(
        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
        text = "Contact Information",
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
        color = BlackColor
    )

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 15.dp),
        shape = RoundedCornerShape(8.dp),
        value = viewModel.name.value,
        onValueChange = { viewModel.updateName(it) },
        label = { Text(text = "Name", fontFamily = FontFamily(Font(Res.font.Inter_Bold))) },
        isError = viewModel.nameError.value,
        singleLine = true,
        leadingIcon = {
            Icon(modifier = Modifier.size(24.dp), painter = painterResource(Res.drawable.icon_person), contentDescription = "Person Icon")
        },
        trailingIcon = {
            if (viewModel.name.value.isNotEmpty()) {
                IconButton(onClick = { viewModel.updateName("") }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear text")
                }
            }
        },
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
        value = viewModel.email.value,
        onValueChange = { viewModel.updateEmail(it) },
        label = { Text(text = "Email", fontFamily = FontFamily(Font(Res.font.Inter_Bold))) },
        isError = viewModel.emailError.value,
        singleLine = true,
        leadingIcon = {
            Icon(modifier = Modifier.size(22.dp), painter = painterResource(Res.drawable.icon_email), contentDescription = "Email Icon")
        },
        trailingIcon = {
            if (viewModel.email.value.isNotEmpty()) {
                IconButton(onClick = { viewModel.updateEmail("") }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear text")
                }
            }
        },
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
        value = viewModel.phone.value,
        onValueChange = { newValue ->
            val filteredValue = newValue.filter { it.isDigit() }
            viewModel.updatePhone(filteredValue)
        },
        label = { Text(text = "Phone", fontFamily = FontFamily(Font(Res.font.Inter_Bold))) },
        isError = viewModel.phoneError.value,
        singleLine = true,
        leadingIcon = {
            Icon(modifier = Modifier.size(20.dp), painter = painterResource(Res.drawable.icon_phone), contentDescription = "Phone Icon")
        },
        trailingIcon = {
            if (viewModel.phone.value.isNotEmpty()) {
                IconButton(onClick = { viewModel.updatePhone("") }) {
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

    var isDepartmentExpanded by remember { mutableStateOf(false) }
    var isCityExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isDepartmentExpanded,
        onExpandedChange = { isDepartmentExpanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 15.dp).menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true),
            shape = RoundedCornerShape(8.dp),
            value = viewModel.selectedDepartment.value,
            onValueChange = { },
            label = { Text("Department", fontFamily = FontFamily(Font(Res.font.Inter_Bold))) },
            isError = viewModel.departmentError.value,
            singleLine = true,
            leadingIcon = {
                Icon(modifier = Modifier.size(22.dp), painter = painterResource(Res.drawable.icon_department), contentDescription = "Department Icon")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDepartmentExpanded)
            },
            readOnly = true,
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
        ExposedDropdownMenu(
            expanded = isDepartmentExpanded,
            onDismissRequest = { isDepartmentExpanded = false }
        ) {
            viewModel.departments.value.forEach { department ->
                DropdownMenuItem(
                    text = { Text(department) },
                    onClick = {
                        viewModel.selectDepartment(department)
                        isDepartmentExpanded = false
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(5.dp))
    val isCityEnabled = viewModel.selectedDepartment.value.isNotEmpty()

    ExposedDropdownMenuBox(
        expanded = isCityExpanded,
        onExpandedChange = { if (isCityEnabled) isCityExpanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 15.dp).menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true),
            shape = RoundedCornerShape(8.dp),
            value = viewModel.selectedCity.value,
            onValueChange = { },
            label = { Text("City", fontFamily = FontFamily(Font(Res.font.Inter_Bold))) },
            isError = viewModel.cityError.value,
            singleLine = true,
            leadingIcon = {
                Icon(modifier = Modifier.size(22.dp), painter = painterResource(Res.drawable.icon_city), contentDescription = "City Icon")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDepartmentExpanded)
            },
            readOnly = true,
            enabled = isCityEnabled,
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
        ExposedDropdownMenu(
            expanded = isCityExpanded,
            onDismissRequest = { isCityExpanded = false }
        ) {
            viewModel.filteredCities.value.forEach { city ->
                DropdownMenuItem(
                    text = { Text(city) },
                    onClick = {
                        viewModel.selectCity(city)
                        isCityExpanded = false
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(5.dp))

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 15.dp),
        shape = RoundedCornerShape(8.dp),
        value = viewModel.address.value,
        onValueChange = { viewModel.updateAddress(it) },
        label = { Text(text = "Address", fontFamily = FontFamily(Font(Res.font.Inter_Bold))) },
        isError = viewModel.addressError.value,
        singleLine = true,
        leadingIcon = {
            Icon(modifier = Modifier.size(21.dp), painter = painterResource(Res.drawable.icon_home), contentDescription = "Home Icon")
        },
        trailingIcon = {
            if (viewModel.address.value.isNotEmpty()) {
                IconButton(onClick = { viewModel.updateAddress("") }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear text")
                }
            }
        },
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
}

@Composable
private fun errorMessageSection(viewModel: RegisterViewModel) {
    if (viewModel.messageContactError.value.isNotEmpty()) {
        Row(modifier = Modifier.padding(top = 30.dp, bottom = 15.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(Res.drawable.icon_warning),
                contentDescription = "Warning Icon",
                tint = ErrorColor
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = viewModel.messageContactError.value,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                fontFamily = FontFamily(Font(Res.font.Inter_Medium)),
                color = ErrorColor
            )
        }
    }
}

@Composable
private fun ContactRegisterButtonSection(viewModel: RegisterViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .padding(top = 15.dp)
            .height(60.dp)
            .clickable(onClick = { viewModel.validateContactInformation() }),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = BlackColor)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.icon_enter),
                    contentDescription = "Enter Icon",
                    tint = WhiteColor
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Next",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.Inter_Bold)),
                    color = WhiteColor
                )
            }

        }
    }
}