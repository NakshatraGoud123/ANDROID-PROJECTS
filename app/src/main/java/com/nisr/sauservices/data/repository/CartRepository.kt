package com.nisr.sauservices.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.nisr.sauservices.data.model.CartModel
import com.nisr.sauservices.data.model.OrderModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CartRepository {
    private val databaseUrl = "https://sau-services-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private val database = FirebaseDatabase.getInstance(databaseUrl)
    private val auth = FirebaseAuth.getInstance()
    
    private fun getUserId(): String {
        val uid = auth.currentUser?.uid
        Log.d("CartRepository", "Current User ID: $uid")
        return uid ?: "anonymous"
    }
    
    private fun getCartRef() = database.getReference("cart").child(getUserId()).child("items")

    suspend fun addToCart(item: CartModel): Result<Unit> = try {
        val ref = getCartRef()
        val key = ref.push().key ?: throw Exception("Failed to generate cart item key")
        val finalItem = item.copy(itemId = key)
        ref.child(key).setValue(finalItem).await()
        Log.d("CartRepository", "Item added to cart: ${item.itemName}")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("CartRepository", "Add to cart failed: ${e.message}")
        Result.failure(e)
    }

    suspend fun updateQuantity(itemId: String, newQuantity: Int): Result<Unit> = try {
        if (itemId.isEmpty()) throw Exception("Item ID is empty")
        if (newQuantity <= 0) {
            removeItem(itemId).await()
        } else {
            val ref = getCartRef().child(itemId)
            ref.child("quantity").setValue(newQuantity).await()
            
            // Re-calculate total price
            val snapshot = ref.get().await()
            val price = snapshot.child("price").getValue(Double::class.java) ?: 0.0
            ref.child("totalPrice").setValue(price * newQuantity).await()
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("CartRepository", "Update quantity failed: ${e.message}")
        Result.failure(e)
    }

    fun getCartItems(): Flow<List<CartModel>> = callbackFlow {
        val userId = getUserId()
        val ref = database.getReference("cart").child(userId).child("items")
        Log.d("CartRepository", "Listening to cart at: cart/$userId/items")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(CartModel::class.java) }
                Log.d("CartRepository", "Cart snapshot changed: ${items.size} items found")
                trySend(items)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("CartRepository", "Cart listener cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun removeItem(itemId: String) = getCartRef().child(itemId).removeValue()
    
    suspend fun clearCart(): Result<Unit> = try {
        getCartRef().removeValue().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun placeOrder(order: OrderModel): Result<String> {
        return try {
            val uid = getUserId()
            val orderRef = database.getReference("orders").child(uid).push()
            val orderId = orderRef.key ?: throw Exception("ID Gen Failed")
            
            val finalOrder = order.copy(orderId = orderId)
            orderRef.setValue(finalOrder).await()
            Log.d("CartRepository", "Order placed: $orderId")
            
            // If it contains bookings, also store in /bookings for the unified list
            order.items?.filter { it.unit == "Booking" }?.forEach { booking ->
                val bRef = database.getReference("bookings").child(uid).push()
                bRef.setValue(finalOrder.copy(
                    orderId = bRef.key ?: "",
                    serviceName = booking.itemName,
                    scheduleDate = booking.date,
                    scheduleTime = booking.time,
                    amount = booking.totalPrice,
                    items = null
                )).await()
            }

            Result.success(orderId)
        } catch (e: Exception) {
            Log.e("CartRepository", "Place order failed: ${e.message}")
            Result.failure(e)
        }
    }
}
