package com.uniandes.modus.ui.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.uniandes.modus.Location
import com.uniandes.modus.getConnectivityChecker
import com.uniandes.modus.getCurrentWeek
import com.uniandes.modus.getCurrentYear
import com.uniandes.modus.model.AuthenticationRepository
import com.uniandes.modus.model.PaymentRepository
import com.uniandes.modus.model.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import modus.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
class RegisterViewModel: ScreenModel {

    private val authenticationRepository = AuthenticationRepository.instance
    private val userRepository = UserRepository.instance
    private val paymentRepository = PaymentRepository.instance

    private val _name = mutableStateOf(value = "")
    val name: State<String> = _name

    private val _email = mutableStateOf(value = "")
    val email: State<String> = _email

    private val _phone = mutableStateOf(value = "")
    val phone: State<String> = _phone

    private val _address = mutableStateOf(value = "")
    val address: State<String> = _address

    private val _document = mutableStateOf(value = "")
    val document: State<String> = _document

    private val _password = mutableStateOf(value = "")
    val password: State<String> = _password

    private val _confirmPassword = mutableStateOf(value = "")
    val confirmPassword: State<String> = _confirmPassword

    private val _nameError = mutableStateOf(value = false)
    val nameError: State<Boolean> = _nameError

    private val _emailError = mutableStateOf(value = false)
    val emailError: State<Boolean> = _emailError

    private val _phoneError = mutableStateOf(value = false)
    val phoneError: State<Boolean> = _phoneError

    private val _departmentError = mutableStateOf(value = false)
    val departmentError: State<Boolean> = _departmentError

    private val _cityError = mutableStateOf(value = false)
    val cityError: State<Boolean> = _cityError

    private val _addressError = mutableStateOf(value = false)
    val addressError: State<Boolean> = _addressError

    private val _documentError = mutableStateOf(value = false)
    val documentError: State<Boolean> = _documentError

    private val _passwordError = mutableStateOf(value = false)
    val passwordError: State<Boolean> = _passwordError

    private val _confirmPasswordError = mutableStateOf(value = false)
    val confirmPasswordError: State<Boolean> = _confirmPasswordError

    private val _messageContactError = mutableStateOf(value = "")
    val messageContactError: State<String> = _messageContactError

    private val _messageAccessError = mutableStateOf(value = "")
    val messageAccessError: State<String> = _messageAccessError

    private val _isLoading = MutableStateFlow(value = false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _navigateToLogin = MutableStateFlow(value = false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin

    private val _navigateToAccessRegister = MutableStateFlow(value = false)
    val navigateToAccessRegister: StateFlow<Boolean> = _navigateToAccessRegister

    private val _navigateToContactRegister = MutableStateFlow(value = false)
    val navigateToContactRegister: StateFlow<Boolean> = _navigateToContactRegister

    private val _navigateToHome = MutableStateFlow(value = false)
    val navigateToHome: StateFlow<Boolean> = _navigateToHome

    fun updateName(newText: String) {
        _name.value = newText
    }

    fun updateEmail(newText: String) {
        _email.value = newText
    }

    fun updatePhone(newText: String) {
        _phone.value = newText
    }

    fun updateAddress(newText: String) {
        _address.value = newText
    }

    fun updateDocument(newText: String) {
        _document.value = newText
    }

    fun updatePassword(newText: String) {
        _password.value = newText
    }

    fun updateConfirmPassword(newText: String) {
        _confirmPassword.value = newText
    }

    fun validateContactInformation() {
        _messageContactError.value = ""
        _nameError.value = false
        _emailError.value = false
        _phoneError.value = false
        _departmentError.value = false
        _cityError.value = false
        _addressError.value = false

        val nameRegex = Regex("^[A-Za-z]+( [A-Za-z]+)*$")
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        val phoneRegex = Regex("^\\d{7,15}\$")
        val addressRegex = Regex("^\\S.*\\S$")

        when {
            name.value.isEmpty() -> {
                _messageContactError.value = "Name cannot be empty"
                _nameError.value = true
                return
            }
            !name.value.matches(nameRegex) -> {
                _messageContactError.value = "Invalid name format"
                _nameError.value = true
                return
            }
            email.value.isEmpty() -> {
                _messageContactError.value = "Email cannot be empty"
                _emailError.value = true
                return
            }
            !email.value.matches(emailRegex) -> {
                _messageContactError.value = "Invalid email format"
                _emailError.value = true
                return
            }
            phone.value.isEmpty() -> {
                _messageContactError.value = "Phone cannot be empty"
                _phoneError.value = true
                return
            }
            !phone.value.matches(phoneRegex) -> {
                _messageContactError.value = "Invalid phone number"
                _phoneError.value = true
                return
            }
            selectedDepartment.value.isEmpty() -> {
                _messageContactError.value = "Department cannot be empty"
                _departmentError.value = true
                return
            }
            selectedCity.value.isEmpty() -> {
                _messageContactError.value = "City cannot be empty"
                _cityError.value = true
                return
            }
            address.value.isEmpty() -> {
                _messageContactError.value = "Address cannot be empty"
                _addressError.value = true
                return
            }
            !address.value.matches(addressRegex) -> {
                _messageContactError.value = "Invalid address format"
                _addressError.value = true
                return
            }
        }

        _navigateToAccessRegister.value = true
    }

    fun validateRegister() {
        _messageAccessError.value = ""
        _documentError.value = false
        _passwordError.value = false
        _confirmPasswordError.value = false

        when {
            document.value.trim().isEmpty() -> {
                _messageAccessError.value = "Document cannot be empty"
                _documentError.value = true
                return
            }
            password.value.isEmpty() -> {
                _messageAccessError.value = "Password cannot be empty"
                _passwordError.value = true
                return
            }
            password.value.length < 6 -> {
                _messageAccessError.value = "Password must have at least 6 characters"
                _passwordError.value = true
                return
            }
            confirmPassword.value.isEmpty() -> {
                _messageAccessError.value = "Confirm password cannot be empty"
                _confirmPasswordError.value = true
                return
            }
            password.value != confirmPassword.value -> {
                _messageAccessError.value = "Passwords do not match"
                _confirmPasswordError.value = true
                return
            }
        }

        screenModelScope.launch {
            if (getConnectivityChecker().isInternetAvailable()) {
                _isLoading.value = true
                if (authenticationRepository.register(email = email.value, password = password.value)) {
                    if (userRepository.add(
                            name = name.value,
                            email = email.value,
                            phone = phone.value,
                            department = selectedDepartment.value,
                            city = selectedCity.value,
                            address = address.value,
                            document = document.value
                        )) {
                        if (paymentRepository.createPaymentsOfYear(
                                document.value,
                                getCurrentYear().toString(),
                                getCurrentWeek().toString())
                        ) {
                            if (authenticationRepository.authenticate(document.value, password.value)) {
                                _navigateToHome.value = true
                            } else {
                                _messageAccessError.value = "Error authenticate user"
                            }
                        } else {
                            _messageAccessError.value = "Error initialize user"
                        }
                    } else {
                        _messageAccessError.value = "Error adding user"
                    }
                } else {
                    _messageAccessError.value = "Error registering user"
                }
                _isLoading.value = false
            } else {
                _messageAccessError.value = "The device is not connected to the internet"
            }
        }
    }

    fun navigateToLoginScreen() {
        _navigateToLogin.value = true
    }

    fun navigateToContactRegisterScreen() {
        _navigateToContactRegister.value = true
    }

    fun resetNavigation() {
        _navigateToLogin.value = false
        _navigateToContactRegister.value = false
        _navigateToAccessRegister.value = false
        _navigateToHome.value = false
    }

    private var locations: List<Location> = emptyList()

    private val _departments = MutableStateFlow<List<String>>(emptyList())
    val departments: StateFlow<List<String>> = _departments

    private val _selectedDepartment = MutableStateFlow("")
    val selectedDepartment: StateFlow<String> = _selectedDepartment

    private val _filteredCities = MutableStateFlow<List<String>>(emptyList())
    val filteredCities: StateFlow<List<String>> = _filteredCities

    private val _selectedCity = MutableStateFlow("")
    val selectedCity: StateFlow<String> = _selectedCity

    init {
        screenModelScope.launch {
            val locationsString = Res.readBytes("files/locations.json").decodeToString()
            locations = Json.decodeFromString<List<Location>>(locationsString)
            val departmentsList = locations.map { it.department }.distinct()
            _departments.value = departmentsList
        }
    }

    fun selectDepartment(department: String) {
        _selectedDepartment.value = department
        val cities = locations
            .filter { it.department == department }
            .map { it.city }
            .sorted()
        _filteredCities.value = cities
        _selectedCity.value = ""
    }

    fun selectCity(city: String) {
        _selectedCity.value = city
    }

}