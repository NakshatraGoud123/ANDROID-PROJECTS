package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.CartModel
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.RealtimeDatabaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class BookingItem(
    val id: String,
    val serviceName: String,
    val date: String,
    val time: String,
    val status: String, // Upcoming, Completed
    val price: String
)

class BookingsViewModel : ViewModel() {
    private val repository = RealtimeDatabaseRepository()
    
    private val _dbBookings = MutableStateFlow<List<OrderModel>>(emptyList())
    val dbBookings = _dbBookings.asStateFlow()

    val bookingsFlow = _dbBookings.map { list ->
        list.map { order ->
            BookingItem(
                id = order.orderId,
                serviceName = order.serviceName.ifEmpty { "General Service" },
                date = order.scheduleDate ?: SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(order.timestamp)),
                time = order.scheduleTime ?: "Anytime",
                status = if (order.status == "success") "Upcoming" else order.status,
                price = "₹${order.amount}"
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // For backward compatibility
    val bookings: List<BookingItem> get() = bookingsFlow.value

    private val _bookingResult = MutableStateFlow<Result<String>?>(null)
    val bookingResult = _bookingResult.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeUserActivity().collect {
                _dbBookings.value = it
            }
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
        items: List<CartModel>? = null
    ) {
        viewModelScope.launch {
            val order = OrderModel(
                serviceName = serviceName,
                category = category,
                subcategory = subcategory,
                scheduleDate = if (date.isBlank()) null else date,
                scheduleTime = if (time.isBlank()) null else time,
                items = items,
                amount = amount,
                paymentMethod = paymentMethod,
                address = address,
                status = "success",
                timestamp = System.currentTimeMillis()
            )
            val result = repository.placeOrderDirectly(order)
            _bookingResult.value = result
        }
    }

    fun addBooking(booking: BookingItem) {
        // Kept for binary compatibility with old code, does nothing now as we rely on DB
    }

    fun resetResult() {
        _bookingResult.value = null
    }
}
