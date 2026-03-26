package com.nisr.sauservices.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nisr.sauservices.data.model.BookingRequest
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.SupplyOrder
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await

class RealtimeDatabaseRepository {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun placeSupplyOrder(order: SupplyOrder): Result<String> = try {
        val userId = getCurrentUserId() ?: "anonymous"
        val ref = database.getReference("orders").child(userId).push()
        val orderId = ref.key ?: throw Exception("Failed to get reference key")
        val finalOrder = order.copy(orderId = orderId, userId = userId)
        ref.setValue(finalOrder).await()
        Result.success(orderId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun placeOrderDirectly(order: OrderModel): Result<String> = try {
        val userId = getCurrentUserId() ?: "anonymous"
        val ref = database.getReference("orders").child(userId).push()
        val id = ref.key ?: throw Exception("Failed to get reference key")
        ref.setValue(order.copy(orderId = id)).await()
        Result.success(id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun observeUserActivity(): Flow<List<OrderModel>> = callbackFlow {
        val userId = getCurrentUserId() ?: "anonymous"
        val ref = database.getReference("orders").child(userId)
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }
                trySend(items)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
