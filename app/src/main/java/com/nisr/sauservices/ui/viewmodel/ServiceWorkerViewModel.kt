package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Booking
import com.nisr.sauservices.data.model.FirestoreBooking
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ServiceWorkerViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _availableBookings = MutableStateFlow<List<FirestoreBooking>>(emptyList())
    val availableBookings = _availableBookings.asStateFlow()

    private val _myBookings = MutableStateFlow<List<FirestoreBooking>>(emptyList())
    val myBookings = _myBookings.asStateFlow()

    // LiveData for traditional XML/View-based Activities like ServiceWorkerDashboardActivity
    private val _bookings = MutableLiveData<List<Booking>>()
    val bookings: LiveData<List<Booking>> = _bookings

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        repository.getCurrentUserId()?.let { userId ->
            loadMyJobs(userId)
        }

        // Bridge StateFlow to LiveData and map FirestoreBooking to Booking
        viewModelScope.launch {
            _myBookings.collect { firestoreList ->
                _bookings.value = firestoreList.map { it.toBooking() }
            }
        }
    }

    private fun FirestoreBooking.toBooking(): Booking {
        return Booking(
            bookingId = this.bookingId,
            customerName = this.customerName,
            serviceType = this.serviceType ?: "General Service",
            address = this.address,
            timeSlot = "Today", // Placeholder as FirestoreBooking doesn't store slot currently
            status = this.status,
            price = if (this.price.isEmpty()) "₹799" else this.price
        )
    }

    fun loadAvailableJobs(serviceType: String?) {
        viewModelScope.launch {
            repository.observeAvailableBookings("worker", serviceType)
                .catch { e -> _errorMessage.value = e.message }
                .collect {
                    _availableBookings.value = it
                }
        }
    }

    fun loadMyJobs(userId: String) {
        viewModelScope.launch {
            repository.observeMyBookings("worker", userId)
                .catch { e -> _errorMessage.value = e.message }
                .collect {
                    _myBookings.value = it
                }
        }
    }

    fun acceptBooking(bookingId: String, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.acceptBooking(bookingId, "worker", userId).onSuccess {
                _errorMessage.value = null
            }.onFailure {
                _errorMessage.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun updateBookingStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, status).onSuccess {
                _errorMessage.value = null
            }.onFailure {
                _errorMessage.value = it.message
            }
            _isLoading.value = false
        }
    }
}
