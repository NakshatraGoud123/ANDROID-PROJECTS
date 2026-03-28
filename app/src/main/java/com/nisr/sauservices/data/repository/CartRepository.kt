package com.nisr.sauservices.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.nisr.sauservices.data.model.BookingModel
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
            val orderRef = database.getReference("orders").push()
            val orderId = orderRef.key ?: throw Exception("ID Gen Failed")
            
            val finalOrder = order.copy(
                orderId = orderId, 
                customerId = uid,
                orderStatus = "pending"
            )
            orderRef.setValue(finalOrder).await()
            
            Log.d("CartRepository", "Order placed: $orderId")
            
            // If it contains bookings, also store in /bookings separately
            order.items.filter { it.unit == "Booking" }.forEach { cartItem ->
                val bRef = database.getReference("bookings").push()
                val bId = bRef.key ?: ""
                val bookingData = BookingModel(
                    bookingId = bId,
                    customerId = uid,
                    serviceId = cartItem.productId,
                    serviceName = cartItem.itemName,
                    scheduledDate = cartItem.date ?: "",
                    scheduledTime = cartItem.time ?: "",
                    status = "pending",
                    address = finalOrder.address,
                    timestamp = System.currentTimeMillis()
                )
                bRef.setValue(bookingData).await()
            }

            Result.success(orderId)
        } catch (e: Exception) {
            Log.e("CartRepository", "Place order failed: ${e.message}")
            Result.failure(e)
        }
    }

    // --- Staff Action Methods ---

    suspend fun updateOrderStatus(orderId: String, newStatus: String, staffId: String? = null): Result<Unit> = try {
        val updates = mutableMapOf<String, Any?>("orderStatus" to newStatus)
        staffId?.let { updates["assignedDeliveryBoy"] = it }

        database.getReference("orders").child(orderId).updateChildren(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateBookingStatus(bookingId: String, newStatus: String, workerId: String? = null): Result<Unit> = try {
        val updates = mutableMapOf<String, Any?>("status" to newStatus)
        workerId?.let { updates["workerId"] = it }

        database.getReference("bookings").child(bookingId).updateChildren(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getGlobalOrders(): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }
                trySend(orders)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getGlobalBookings(): Flow<List<BookingModel>> = callbackFlow {
        val ref = database.getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookings = snapshot.children.mapNotNull { it.getValue(BookingModel::class.java) }
                trySend(bookings)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
