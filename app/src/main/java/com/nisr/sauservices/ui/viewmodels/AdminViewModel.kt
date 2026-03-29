package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.FirebaseUser
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _allBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val allBookings = _allBookings.asStateFlow()

    private val _allUsers = MutableStateFlow<List<FirebaseUser>>(emptyList())
    val allUsers = _allUsers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        observeAllBookings()
        loadAllUsers()
    }

    private fun observeAllBookings() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.observeAllBookings().collect { list ->
                _allBookings.value = list.map { it.toBookingModel() }
                _isLoading.value = false
            }
        }
    }

    fun loadAllUsers() {
        viewModelScope.launch {
            _allUsers.value = repository.getAllUsers()
        }
    }

    fun updateBookingStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, status)
        }
    }

    fun reassignWorker(bookingId: String, workerId: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, "assigned", workerId = workerId)
        }
    }

    fun deleteUser(uid: String) {
        viewModelScope.launch {
            repository.deleteUser(uid).onSuccess {
                loadAllUsers()
            }
        }
    }

    private fun com.nisr.sauservices.data.model.FirestoreBooking.toBookingModel() = BookingModel(
        bookingId = this.bookingId,
        serviceName = this.serviceType ?: "",
        status = this.status,
        address = this.address
    )
}
