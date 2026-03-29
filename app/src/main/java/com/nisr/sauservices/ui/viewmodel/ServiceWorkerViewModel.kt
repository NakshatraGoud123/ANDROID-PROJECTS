package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Booking
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.FirestoreBooking
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServiceWorkerViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _pendingBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val pendingBookings = _pendingBookings.asStateFlow()

    private val _myBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val myBookings = _myBookings.asStateFlow()

    // LiveData for compatibility with Activity and existing Compose Screens
    private val _bookings = MutableLiveData<List<Booking>>(emptyList())
    val bookings: LiveData<List<Booking>> = _bookings

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Compatibility for screens expecting availableBookings
    private val _availableBookings = MutableStateFlow<List<FirestoreBooking>>(emptyList())
    val availableBookings = _availableBookings.asStateFlow()

    init {
        observePendingBookings()
        observeAcceptedBookings()
    }

    fun observePendingBookings() {
        viewModelScope.launch {
            repository.getPendingBookings().collect { list ->
                _pendingBookings.value = list
                _availableBookings.value = list.map { it.toFirestoreBooking() }
            }
        }
    }

    private fun observeAcceptedBookings() {
        repository.getCurrentUserId()?.let { userId ->
            viewModelScope.launch {
                repository.observeWorkerBookings(userId).collect { list ->
                    _myBookings.value = list
                    _bookings.value = list.map { it.toLegacyBooking() }
                }
            }
        }
    }

    fun acceptBooking(bookingId: String) {
        val userId = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, "accepted", workerId = userId)
            _isLoading.value = false
        }
    }

    fun updateBookingStatus(bookingId: String, status: String) {
        val userId = repository.getCurrentUserId()
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, status, workerId = if (status == "accepted") userId else null)
            _isLoading.value = false
        }
    }

    fun completeBooking(bookingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, "completed")
            _isLoading.value = false
        }
    }

    private fun BookingModel.toLegacyBooking() = Booking(
        bookingId = bookingId,
        customerName = "Customer",
        serviceType = serviceName,
        address = address,
        timeSlot = scheduledTime,
        status = status
    )

    private fun BookingModel.toFirestoreBooking() = FirestoreBooking(
        bookingId = bookingId,
        customerName = "Customer",
        serviceType = serviceName,
        address = address,
        status = status
    )
}
