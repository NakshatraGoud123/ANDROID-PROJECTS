package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.CartModel
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BookingItem(
    val id: String = "",
    val serviceName: String = "",
    val price: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = ""
)

class BookingsViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _bookingResult = MutableStateFlow<Result<String>?>(null)
    val bookingResult = _bookingResult.asStateFlow()

    private val _myBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val myBookings = _myBookings.asStateFlow()
    
    private val _bookingsFlow = MutableStateFlow<List<BookingItem>>(emptyList())
    val bookingsFlow = _bookingsFlow.asStateFlow()

    init {
        loadUserBookings()
    }

    fun addBooking(item: BookingItem) {
        _bookingsFlow.value = _bookingsFlow.value + item
    }

    fun bookService(
        serviceId: String,
        serviceName: String,
        date: String,
        time: String,
        address: String
    ) {
        viewModelScope.launch {
            val booking = BookingModel(
                customerId = repository.getCurrentUserId() ?: "",
                serviceId = serviceId,
                serviceName = serviceName,
                scheduledDate = date,
                scheduledTime = time,
                address = address,
                status = "pending"
            )
            val result = repository.bookService(booking)
            _bookingResult.value = result
        }
    }

    fun placeUnifiedOrder(
        serviceName: String,
        category: String,
        subcategory: String,
        date: String,
        time: String,
        amount: Double,
        paymentMethod: String,
        address: String,
        items: List<CartModel>
    ) {
        viewModelScope.launch {
            val order = OrderModel(
                customerId = repository.getCurrentUserId() ?: "",
                items = items,
                totalPrice = amount,
                address = address,
                orderStatus = "pending",
                serviceName = serviceName,
                category = category,
                subcategory = subcategory,
                scheduleDate = date,
                scheduleTime = time,
                amount = amount,
                paymentMethod = paymentMethod
            )
            val result = repository.placeOrder(order)
            _bookingResult.value = result
        }
    }

    fun loadUserBookings() {
        val userId = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            repository.observeMyBookings("customer", userId).collect { list ->
                _myBookings.value = list
                _bookingsFlow.value = list.map {
                    BookingItem(
                        id = it.bookingId,
                        serviceName = it.serviceName,
                        price = "₹0.0",
                        date = it.scheduledDate,
                        time = it.scheduledTime,
                        status = it.status
                    )
                }
            }
        }
    }

    fun resetResult() {
        _bookingResult.value = null
    }
}
