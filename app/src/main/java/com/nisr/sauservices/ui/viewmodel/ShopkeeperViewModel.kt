package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nisr.sauservices.data.model.Order
import com.nisr.sauservices.data.model.InventoryItem
import com.nisr.sauservices.data.repository.DashboardRepository

class ShopkeeperViewModel : ViewModel() {
    private val repository = DashboardRepository()
    
    private val _orders = repository.getOrders() as MutableLiveData<List<Order>>
    val orders: LiveData<List<Order>> = _orders
    
    private val _inventory = repository.getInventory() as MutableLiveData<List<InventoryItem>>
    val inventory: LiveData<List<InventoryItem>> = _inventory

    fun updateOrderStatus(orderId: String, newStatus: String) {
        val currentOrders = _orders.value?.toMutableList() ?: return
        val index = currentOrders.indexOfFirst { it.orderId == orderId }
        if (index != -1) {
            currentOrders[index] = currentOrders[index].copy(status = newStatus)
            _orders.value = currentOrders
        }
    }
    
    fun updateStock(itemId: String, delta: Int) {
        val currentInventory = _inventory.value?.toMutableList() ?: return
        val index = currentInventory.indexOfFirst { it.id == itemId }
        if (index != -1) {
            val newItem = currentInventory[index].copy(
                stockCount = (currentInventory[index].stockCount + delta).coerceAtLeast(0)
            )
            currentInventory[index] = newItem.copy(
                status = if (newItem.stockCount == 0) "Out of Stock" else if (newItem.stockCount < 10) "Low Stock" else "In Stock"
            )
            _inventory.value = currentInventory
        }
    }
}
