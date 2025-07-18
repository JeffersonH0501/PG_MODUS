package com.uniandes.modus.ui.garments.user

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.uniandes.modus.getConnectivityChecker
import com.uniandes.modus.model.AuthenticationRepository
import com.uniandes.modus.model.GarmentRepository
import com.uniandes.modus.model.UserSaveGarmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GarmentsUserViewModel: ScreenModel {

    private val garmentRepository = GarmentRepository.instance
    private val userSaveGarmentRepository = UserSaveGarmentRepository.instance
    private val authRepository = AuthenticationRepository.instance

    private val _garments = MutableStateFlow<List<GarmentRepository.Garment>>(emptyList())
    val garments: StateFlow<List<GarmentRepository.Garment>> = _garments

    private val _savedGarments = MutableStateFlow<List<GarmentRepository.Garment>>(emptyList())
    val savedGarments: StateFlow<List<GarmentRepository.Garment>> = _savedGarments

    private val _garmentSelected = MutableStateFlow<GarmentRepository.Garment?>(null)
    val garmentSelected: StateFlow<GarmentRepository.Garment?> = _garmentSelected

    private val _categorySelected = MutableStateFlow<String?>(null)
    val categorySelected: StateFlow<String?> = _categorySelected

    private val _isScreenGarmentsLoading = MutableStateFlow(true)
    val isScreenGarmentsLoading: StateFlow<Boolean> = _isScreenGarmentsLoading

    private val _isScreenGarmentsRefreshing = MutableStateFlow(false)
    val isScreenGarmentsRefreshing: StateFlow<Boolean> = _isScreenGarmentsRefreshing

    private val _isDeviceWhitOutConnectionScreenGarments = MutableStateFlow(false)
    val isDeviceWhitOutConnectionScreenGarments: StateFlow<Boolean> = _isDeviceWhitOutConnectionScreenGarments

    init {
        screenModelScope.launch {
            loadData()
            getConnectivityChecker().observeConnectivity().collect { isConnected ->
                if (isConnected) {
                    _isDeviceWhitOutConnectionScreenGarments.value = false
                    _isScreenGarmentsLoading.value = false
                    userSaveGarmentRepository.syncGarmentQueue()
                } else if (_garments.value.isNotEmpty()) {
                    _isScreenGarmentsLoading.value = false
                }
            }
        }
    }

    private suspend fun loadData() {
        _garments.value = garmentRepository.getGarments()
        _isDeviceWhitOutConnectionScreenGarments.value = _garments.value.isEmpty()
        garmentRepository.updateGarmentsFromNetwork()
        authRepository.getCurrentUser()?.let {
            _savedGarments.value = userSaveGarmentRepository.getSavedGarmentsByUserDocument(it.document)
        }
    }

    fun refreshGarments() {
        screenModelScope.launch {
            _isScreenGarmentsRefreshing.value = true
            loadData()
            _isScreenGarmentsRefreshing.value = false
        }
    }

    fun setCategorySelected(category: String) {
        _categorySelected.value = category
    }

    fun setGarmentSelected(garment: GarmentRepository.Garment) {
        _garmentSelected.value = garment
    }

    fun saveGarment(garment: GarmentRepository.Garment) {
        _savedGarments.value = listOf(garment) + _savedGarments.value
        screenModelScope.launch {
            authRepository.getCurrentUser()?.let {
                userSaveGarmentRepository.saveGarment(it.document, garment.reference)
            }
        }
    }

    fun notSaveGarment(garment: GarmentRepository.Garment) {
        _savedGarments.value -= garment
        screenModelScope.launch {
            authRepository.getCurrentUser()?.let {
                userSaveGarmentRepository.notSaveGarment(it.document, garment.reference)
            }
        }
    }

    sealed class GarmentsNavigationEvent {
        data object None : GarmentsNavigationEvent()

        data object NavigateToHomeFromGarments : GarmentsNavigationEvent()
        data object NavigateToStoresLocationFromGarments : GarmentsNavigationEvent()
        data object NavigateToGarmentsCategoryFromGarments : GarmentsNavigationEvent()
        data object NavigateToGarmentsDetailFromGarments : GarmentsNavigationEvent()

        data object NavigateToGarmentsFromStoresLocation : GarmentsNavigationEvent()

        data object NavigateToGarmentsFromGarmentsCategory : GarmentsNavigationEvent()
        data object NavigateToGarmentsDetailFromGarmentsCategory : GarmentsNavigationEvent()

        data object NavigateToBackFromGarmentsDetail : GarmentsNavigationEvent()
    }

    private val _navigationEvent = MutableStateFlow<GarmentsNavigationEvent>(GarmentsNavigationEvent.None)
    val navigationEvent: StateFlow<GarmentsNavigationEvent> = _navigationEvent

    fun navigateToHomeScreenFromGarmentsScreen() {
        _navigationEvent.value = GarmentsNavigationEvent.NavigateToHomeFromGarments
    }

    fun navigateToStoresLocationScreenFromGarmentsScreen() {
        _navigationEvent.value = GarmentsNavigationEvent.NavigateToStoresLocationFromGarments
    }

    fun navigateToGarmentsCategoryScreenFromGarmentsScreen() {
        _navigationEvent.value = GarmentsNavigationEvent.NavigateToGarmentsCategoryFromGarments
    }

    fun navigateToGarmentsDetailScreenFromGarmentsScreen() {
        _navigationEvent.value = GarmentsNavigationEvent.NavigateToGarmentsDetailFromGarments
    }

    fun navigateToGarmentsScreenFromStoresLocationScreen() {
        _navigationEvent.value = GarmentsNavigationEvent.NavigateToGarmentsFromStoresLocation
    }

    fun navigateToGarmentsScreenFromGarmentsCategoryScreen() {
        _navigationEvent.value = GarmentsNavigationEvent.NavigateToGarmentsFromGarmentsCategory
    }

    fun navigateToGarmentsDetailScreenFromGarmentsCategoryScreen() {
        _navigationEvent.value = GarmentsNavigationEvent.NavigateToGarmentsDetailFromGarmentsCategory
    }

    fun navigateToBackScreenFromGarmentsDetailScreen() {
        _navigationEvent.value = GarmentsNavigationEvent.NavigateToBackFromGarmentsDetail
    }

    fun resetNavigation() {
        _navigationEvent.value = GarmentsNavigationEvent.None
    }
}