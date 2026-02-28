package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nisr.sauservices.data.model.Order
import com.nisr.sauservices.data.repository.DashboardRepository

class ShopkeeperViewModel : ViewModel() {
    private val repository = DashboardRepository()
    val orders: LiveData<List<Order>> = repository.getOrders()

    fun updateOrderStatus(order: Order, newStatus: String) {
        order.status = newStatus
    }
}
