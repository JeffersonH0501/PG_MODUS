package com.uniandes.modus.ui.payments.user

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.uniandes.modus.getConnectivityChecker
import com.uniandes.modus.getCurrentWeek
import com.uniandes.modus.getCurrentYear
import com.uniandes.modus.model.AuthenticationRepository
import com.uniandes.modus.model.PaymentRepository
import com.uniandes.modus.model.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentsUserViewModel: ScreenModel {

    private val authenticationRepository = AuthenticationRepository.instance
    private val paymentRepository = PaymentRepository.instance
    private val reviewRepository = ReviewRepository.instance

    private val _paidWeeks = MutableStateFlow(0)
    val paidWeeks: StateFlow<Int> = _paidWeeks

    private val _unpaidWeeks = MutableStateFlow(0)
    val unpaidWeeks: StateFlow<Int> = _unpaidWeeks

    private val _missingWeeks = MutableStateFlow(0)
    val missingWeeks: StateFlow<Int> = _missingWeeks

    private val _yearSelected = MutableStateFlow("")
    val yearSelected: StateFlow<String> = _yearSelected

    private val _weekSelected = MutableStateFlow("")
    val weekSelected: StateFlow<String> = _weekSelected

    private val _payments = MutableStateFlow<List<PaymentRepository.Payment>>(emptyList())
    val payments: StateFlow<List<PaymentRepository.Payment>> = _payments

    private val _currentPayment = MutableStateFlow<PaymentRepository.Payment?>(null)
    val currentPayment: StateFlow<PaymentRepository.Payment?> = _currentPayment

    private val _paymentSelected = MutableStateFlow<PaymentRepository.Payment?>(null)
    val paymentSelected: StateFlow<PaymentRepository.Payment?> = _paymentSelected

    private val _reviewSelected = MutableStateFlow<ReviewRepository.Review?>(null)
    val reviewSelected: StateFlow<ReviewRepository.Review?> = _reviewSelected

    private var voucherSelectedByteArray: ByteArray? = null

    private val _isScreenPaymentsLoading = MutableStateFlow(true)
    val isScreenPaymentsLoading: StateFlow<Boolean> = _isScreenPaymentsLoading

    private val _isScreenPaymentsRefreshing = MutableStateFlow(false)
    val isScreenPaymentsRefreshing: StateFlow<Boolean> = _isScreenPaymentsRefreshing

    private val _isScreenPaymentsDetailLoading = MutableStateFlow(true)
    val isScreenPaymentsDetailLoading: StateFlow<Boolean> = _isScreenPaymentsDetailLoading

    private val _isLoadingUploading = MutableStateFlow(false)
    val isLoadingUploading: StateFlow<Boolean> = _isLoadingUploading

    private val _isLoadingDeleting = MutableStateFlow(false)
    val isLoadingDeleting: StateFlow<Boolean> = _isLoadingDeleting

    private val _navigateToHomeFromPaymentsUser = MutableStateFlow(false)
    val navigateToHomeFromPaymentsUser: StateFlow<Boolean> = _navigateToHomeFromPaymentsUser

    private val _navigateToPaymentsDetailFromPaymentsUser = MutableStateFlow(false)
    val navigateToPaymentsDetailFromPaymentsUser: StateFlow<Boolean> = _navigateToPaymentsDetailFromPaymentsUser

    private val _navigateToPaymentsFromPaymentsDetailUser = MutableStateFlow(false)
    val navigateToPaymentsFromPaymentsDetailUser: StateFlow<Boolean> = _navigateToPaymentsFromPaymentsDetailUser

    private val _isDeviceWhitOutConnection = MutableStateFlow(false)
    val isDeviceWhitOutConnection: StateFlow<Boolean> = _isDeviceWhitOutConnection

    private val _showDialogDeviceWhitOutConnection = MutableStateFlow(false)
    val showDialogDeviceWhitOutConnection: StateFlow<Boolean> = _showDialogDeviceWhitOutConnection

    private suspend fun loadData() {
        _yearSelected.value = getCurrentYear().toString()

        _payments.value = authenticationRepository.getCurrentUser()?.let { user ->
            paymentRepository.getYearPaymentsByUser(user.document, _yearSelected.value)
                .sortedBy { it.week.toInt() }
        } ?: emptyList()

        _isDeviceWhitOutConnection.value = _payments.value.isEmpty()

        _paidWeeks.value = _payments.value.count { it.state == "1" }
        _unpaidWeeks.value = _payments.value.count { (it.state == "2" || it.state == "3" || it.state == "4") }
        _missingWeeks.value = _payments.value.count { it.state == "6" }

        _currentPayment.value = _payments.value.find { it.week == getCurrentWeek().toString() }
    }

    init {
        screenModelScope.launch {
            loadData()
            _isScreenPaymentsLoading.value = false
        }
    }

    fun refreshScreenPayments() {
        screenModelScope.launch {
            _isScreenPaymentsRefreshing.value = true
            loadData()
            _isScreenPaymentsRefreshing.value = false
        }
    }

    fun selectVoucher(fileBytes: ByteArray) {
        voucherSelectedByteArray = fileBytes
    }

    fun uploadVoucher() {
        screenModelScope.launch {
            if (getConnectivityChecker().isInternetAvailable()) {
                _isLoadingUploading.value = true
                if (voucherSelectedByteArray != null) {
                    if (_currentPayment.value != null) {
                        if (reviewRepository.add(
                                userDocument = paymentSelected.value!!.userDocument,
                                year = paymentSelected.value!!.year,
                                week = paymentSelected.value!!.week,
                                voucherByteArray = voucherSelectedByteArray!!
                            )) {
                            if (paymentRepository.updateStateOfPayment(
                                    userDocument = paymentSelected.value!!.userDocument,
                                    year = paymentSelected.value!!.year,
                                    week = paymentSelected.value!!.week,
                                    state = "3"
                                )) {
                                _paymentSelected.value?.let { selectedPayment ->
                                    val updatedPayment = selectedPayment.copy(state = "3")

                                    _paymentSelected.value = updatedPayment
                                    _currentPayment.value = updatedPayment
                                    _reviewSelected.value = reviewRepository.getLatestReviewOfWeekPaymentByUser(updatedPayment.userDocument, updatedPayment.year, updatedPayment.week)

                                    _payments.value = _payments.value.map { payment ->
                                        if (payment.userDocument == updatedPayment.userDocument &&
                                            payment.year == updatedPayment.year &&
                                            payment.week == updatedPayment.week
                                        ) {
                                            updatedPayment
                                        } else {
                                            payment
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                _isLoadingUploading.value = false
            } else {
                _showDialogDeviceWhitOutConnection.value = true
            }
        }
    }

    fun deleteReview() {
        screenModelScope.launch {
            if (getConnectivityChecker().isInternetAvailable()) {
                _isLoadingDeleting.value = true
                if (reviewSelected.value != null) {
                    if (reviewRepository.deleteReview(
                            userDocument = reviewSelected.value!!.userDocument,
                            year = reviewSelected.value!!.year,
                            week = reviewSelected.value!!.week,
                            number = reviewSelected.value!!.number,
                            voucher = reviewSelected.value!!.voucher
                        )) {
                        if (paymentRepository.updateStateOfPayment(
                                userDocument = paymentSelected.value!!.userDocument,
                                year = paymentSelected.value!!.year,
                                week = paymentSelected.value!!.week,
                                state = "2"
                            )) {
                            _paymentSelected.value?.let { selectedPayment ->
                                val updatedPayment = selectedPayment.copy(state = "2")

                                _paymentSelected.value = updatedPayment
                                _currentPayment.value = updatedPayment
                                _reviewSelected.value = null

                                _payments.value = _payments.value.map { payment ->
                                    if (payment.userDocument == updatedPayment.userDocument &&
                                        payment.year == updatedPayment.year &&
                                        payment.week == updatedPayment.week
                                    ) {
                                        updatedPayment
                                    } else {
                                        payment
                                    }
                                }
                            }
                        }
                    }
                }
                _isLoadingDeleting.value = false
            } else {
                _showDialogDeviceWhitOutConnection.value = true
            }
        }
    }

    fun hideShowDialogDeviceWhitOutConnection() {
        _showDialogDeviceWhitOutConnection.value = false
    }

    fun navigateToHomeScreenFromPaymentsUser() {
        _navigateToHomeFromPaymentsUser.value = true
    }

    fun navigateToPaymentsDetailScreenFromPaymentsUser(payment: PaymentRepository.Payment?) {
        screenModelScope.launch {
            _navigateToPaymentsDetailFromPaymentsUser.value = true
            _paymentSelected.value = payment
            _weekSelected.value = payment?.week ?: ""
            _reviewSelected.value = reviewRepository.getLatestReviewOfWeekPaymentByUser(payment!!.userDocument, payment.year, payment.week)
            _isScreenPaymentsDetailLoading.value = false
        }
    }

    fun navigateToPaymentsScreenFromPaymentsDetailUser() {
        _navigateToPaymentsFromPaymentsDetailUser.value = true
    }

    fun resetNavigation() {
        _navigateToHomeFromPaymentsUser.value = false
        _navigateToPaymentsDetailFromPaymentsUser.value = false
        _navigateToPaymentsFromPaymentsDetailUser.value = false
    }
}