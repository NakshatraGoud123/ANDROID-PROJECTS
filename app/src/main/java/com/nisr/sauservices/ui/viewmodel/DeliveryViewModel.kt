package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nisr.sauservices.data.model.Delivery
import com.nisr.sauservices.data.repository.DashboardRepository

class DeliveryViewModel : ViewModel() {
    private val repository = DashboardRepository()
    val deliveries: LiveData<List<Delivery>> = repository.getDeliveries()

    fun updateDeliveryStatus(delivery: Delivery, newStatus: String) {
        delivery.status = newStatus
    }
}
