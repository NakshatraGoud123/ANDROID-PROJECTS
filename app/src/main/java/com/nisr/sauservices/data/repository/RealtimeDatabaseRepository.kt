package com.nisr.sauservices.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.SupplyOrder
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RealtimeDatabaseRepository {
    private val databaseUrl = "https://sau-services-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private val database = FirebaseDatabase.getInstance(databaseUrl)
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun placeSupplyOrder(order: SupplyOrder): Result<String> = try {
        val userId = getCurrentUserId() ?: "anonymous"
        // Using a more specific path to avoid mixing types under "orders"
        val ref = database.getReference("supply_orders").child(userId).push()
        val orderId = ref.key ?: throw Exception("Failed to get reference key")
        val finalOrder = order.copy(orderId = orderId, userId = userId)
        ref.setValue(finalOrder).await()
        Result.success(orderId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun placeOrderDirectly(order: OrderModel): Result<String> = try {
        val userId = getCurrentUserId() ?: "anonymous"
        // Consistent with FirebaseRepository: flat structure
        val ref = database.getReference("orders").push()
        val id = ref.key ?: throw Exception("Failed to get reference key")
        val finalOrder = order.copy(orderId = id, customerId = userId)
        ref.setValue(finalOrder).await()
        
        // Also store under /bookings if it has a schedule
        if (order.scheduleDate != null) {
            database.getReference("bookings").child(id).setValue(finalOrder).await()
        }

        Result.success(id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun observeUserActivity(): Flow<List<OrderModel>> = callbackFlow {
        val userId = getCurrentUserId() ?: "anonymous"
        // Now using query on flat structure
        val ref = database.getReference("orders").orderByChild("customerId").equalTo(userId)
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { child ->
                    try {
                        if (child.value is Map<*, *>) child.getValue(OrderModel::class.java) else null
                    } catch (e: Exception) { null }
                }
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
