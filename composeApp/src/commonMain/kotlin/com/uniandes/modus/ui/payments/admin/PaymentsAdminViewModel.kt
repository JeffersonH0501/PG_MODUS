package com.uniandes.modus.ui.payments.admin

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.uniandes.modus.getCurrentWeek
import com.uniandes.modus.getCurrentYear
import com.uniandes.modus.getNowInstant
import com.uniandes.modus.getTotalWeeksInYear
import com.uniandes.modus.model.CardRepository
import com.uniandes.modus.model.PaymentRepository
import com.uniandes.modus.model.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentsAdminViewModel: ScreenModel {

    private val paymentRepository = PaymentRepository.instance
    private val reviewRepository = ReviewRepository.instance
    private val cardRepository = CardRepository.instance

    private val _yearSelected = MutableStateFlow("")
    val yearSelected: StateFlow<String> = _yearSelected

    private val _weekSelected = MutableStateFlow("")
    val weekSelected: StateFlow<String> = _weekSelected

    private val _currentWeek = MutableStateFlow("")
    val currentWeek: StateFlow<String> = _currentWeek

    private val _selectedNumberOfPendingReviews = MutableStateFlow("")
    val selectedNumberOfPendingReviews: StateFlow<String> = _selectedNumberOfPendingReviews

    private val _reviews = MutableStateFlow(HashMap<String, List<ReviewRepository.Review>>(emptyMap()))
    val reviews: StateFlow<HashMap<String, List<ReviewRepository.Review>>> = _reviews

    private val _reviewSelected = MutableStateFlow<ReviewRepository.Review?>(null)
    val reviewSelected: StateFlow<ReviewRepository.Review?> = _reviewSelected

    private val _documentFilter = MutableStateFlow("")
    val documentFilter: StateFlow<String> = _documentFilter

    private val _isScreenPaymentsDetailLoading = MutableStateFlow(true)
    val isScreenPaymentsDetailLoading: StateFlow<Boolean> = _isScreenPaymentsDetailLoading

    private val _isScreenPaymentsDetailRefreshing = MutableStateFlow(false)
    val isScreenPaymentsDetailRefreshing: StateFlow<Boolean> = _isScreenPaymentsDetailRefreshing

    private val _navigateToHomeFromPaymentsAdmin = MutableStateFlow(false)
    val navigateToHomeFromPaymentsAdmin: StateFlow<Boolean> = _navigateToHomeFromPaymentsAdmin

    private val _navigateToPaymentsDetailFromPaymentsAdmin = MutableStateFlow(false)
    val navigateToPaymentsDetailFromPaymentsAdmin: StateFlow<Boolean> = _navigateToPaymentsDetailFromPaymentsAdmin

    private val _navigateToReviewDetailFromPaymentsDetail = MutableStateFlow(false)
    val navigateToReviewDetailFromPaymentsDetail: StateFlow<Boolean> = _navigateToReviewDetailFromPaymentsDetail

    private val _navigateToPaymentsAdminFromPaymentsDetail = MutableStateFlow(false)
    val navigateToPaymentsAdminFromPaymentsDetail: StateFlow<Boolean> = _navigateToPaymentsAdminFromPaymentsDetail

    private val _navigateToPaymentsDetailFromReviewDetail = MutableStateFlow(false)
    val navigateToPaymentsDetailFromReviewDetail: StateFlow<Boolean> = _navigateToPaymentsDetailFromReviewDetail

    private suspend fun loadData() {
        _yearSelected.value = getCurrentYear().toString()
        _currentWeek.value = getCurrentWeek().toString()

        val reviewsByYear = reviewRepository.getReviewsByYear(_yearSelected.value)
        val groupedReviews: Map<String, List<ReviewRepository.Review>> = reviewsByYear.groupBy { it.week }

        val fullReviewsByWeek = HashMap<String, List<ReviewRepository.Review>>()
        for (week in 1..getTotalWeeksInYear(_yearSelected.value.toInt())) {
            fullReviewsByWeek[week.toString()] = (groupedReviews[week.toString()] ?: emptyList())
                .sortedByDescending { it.number }
        }

        _weekSelected.value = _weekSelected.value.ifEmpty { _currentWeek.value }

        _reviews.value = fullReviewsByWeek
        _selectedNumberOfPendingReviews.value = _reviews.value[_weekSelected.value]?.count { it.state == "3" }.toString()
    }

    init {
        screenModelScope.launch {
            loadData()
            _isScreenPaymentsDetailLoading.value = false
        }
    }

    fun refreshScreenPaymentsDetail() {
        screenModelScope.launch {
            _isScreenPaymentsDetailRefreshing.value = true
            loadData()
            _isScreenPaymentsDetailRefreshing.value = false
        }
    }

    fun updateDocumentFilter(document: String) {
        _documentFilter.value = document
    }

    fun approveVoucher() {
        screenModelScope.launch {
            _reviewSelected.value?.let { review ->
                val approved = reviewRepository.approveVoucher(
                    userDocument =  review.userDocument,
                    year = review.year,
                    week = review.week,
                    number = review.number
                )
                if (approved) {

                    val reviewModify = review.copy(
                        state = "1",
                        dateReview = getNowInstant()
                    )

                    _reviewSelected.value = reviewModify
                    _reviews.value = _reviews.value.mapValues { (_, reviews) ->
                        reviews.map { if (it.userDocument == review.userDocument) reviewModify else it }
                            .sortedByDescending { it.number }
                    } as HashMap<String, List<ReviewRepository.Review>>

                    _selectedNumberOfPendingReviews.value = _reviews.value[_weekSelected.value]?.count { it.state == "3" }.toString()

                    paymentRepository.updateStateOfPayment(
                        userDocument = review.userDocument,
                        year = review.year,
                        week = review.week,
                        state = "1"
                    )

                    cardRepository.createCard(review.userDocument, review.year, review.week)
                }
            }
        }
    }

    fun rejectVoucher(reason: String) {
        screenModelScope.launch {
            reviewSelected.value?.let { review ->
                val rejected = reviewRepository.rejectVoucher(
                    review.userDocument,
                    review.year,
                    review.week,
                    review.number,
                    reason
                )
                if (rejected) {
                    val reviewModify = review.copy(
                        state = "2",
                        dateReview = getNowInstant(),
                        description = reason
                    )

                    _reviewSelected.value = reviewModify
                    _reviews.value = _reviews.value.mapValues { (_, reviews) ->
                        reviews.map { if (it.userDocument == review.userDocument) reviewModify else it }
                            .sortedByDescending { it.number }
                    } as HashMap<String, List<ReviewRepository.Review>>

                    _selectedNumberOfPendingReviews.value = _reviews.value[_weekSelected.value]?.count { it.state == "3" }.toString()

                    paymentRepository.updateStateOfPayment(
                        userDocument = review.userDocument,
                        year = review.year,
                        week = review.week,
                        state = "2"
                    )
                }
            }
        }
    }

    fun navigateToHomeScreen() {
        _navigateToHomeFromPaymentsAdmin.value = true
    }

    fun navigateToPaymentsDetailFromPaymentsAdminScreen(weekSelected: String) {
        _navigateToPaymentsDetailFromPaymentsAdmin.value = true
        _weekSelected.value = weekSelected
        _selectedNumberOfPendingReviews.value = _reviews.value[weekSelected]?.count { it.state == "3" }.toString()
    }

    fun navigateToReviewDetailFromPaymentsDetailScreen(review: ReviewRepository.Review) {
        _navigateToReviewDetailFromPaymentsDetail.value = true
        _reviewSelected.value = review
    }

    fun navigateToPaymentsAdminFromPaymentsDetailScreen() {
        _navigateToPaymentsAdminFromPaymentsDetail.value = true
        _selectedNumberOfPendingReviews.value = _reviews.value[_currentWeek.value]?.count { it.state == "3" }.toString()
    }

    fun navigateToPaymentsDetailFromReviewDetailScreen() {
        _navigateToPaymentsDetailFromReviewDetail.value = true
    }

    fun resetNavigation() {
        _navigateToHomeFromPaymentsAdmin.value = false
        _navigateToPaymentsDetailFromPaymentsAdmin.value = false
        _navigateToReviewDetailFromPaymentsDetail.value = false
        _navigateToPaymentsAdminFromPaymentsDetail.value = false
        _navigateToPaymentsDetailFromReviewDetail.value = false
    }
}