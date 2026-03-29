package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _bookingStatus = MutableStateFlow<Result<String>?>(null)
    val bookingStatus = _bookingStatus.asStateFlow()

    private val _myBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val myBookings = _myBookings.asStateFlow()

    fun bookService(booking: BookingModel) {
        viewModelScope.launch {
            _bookingStatus.value = repository.bookService(booking)
        }
    }

    fun observeMyBookings(userId: String) {
        viewModelScope.launch {
            repository.observeMyBookings("customer", userId).collect {
                _myBookings.value = it
            }
        }
    }

    fun resetStatus() {
        _bookingStatus.value = null
    }
}
