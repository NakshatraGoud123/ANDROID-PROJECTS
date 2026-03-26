package com.nisr.sauservices.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nisr.sauservices.data.model.CartModel
import com.nisr.sauservices.data.model.OrderModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CartRepository {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId get() = auth.currentUser?.uid ?: "anonymous"

    private val cartRef = database.getReference("cart").child(userId).child("items")

    fun addToCart(item: CartModel) {
        val key = cartRef.push().key ?: return
        cartRef.child(key).setValue(item.copy(itemId = key))
    }

    fun updateItem(item: CartModel) {
        if (item.itemId.isNotEmpty()) {
            cartRef.child(item.itemId).setValue(item)
        }
    }

    fun getCartItems(): Flow<List<CartModel>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(CartModel::class.java) }
                trySend(items)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        cartRef.addValueEventListener(listener)
        awaitClose { cartRef.removeEventListener(listener) }
    }

    fun removeItem(itemId: String) {
        cartRef.child(itemId).removeValue()
    }

    fun clearCart() {
        cartRef.removeValue()
    }

    suspend fun placeOrder(order: OrderModel): Result<String> {
        return try {
            val orderRef = database.getReference("orders").child(userId).push()
            val orderId = orderRef.key ?: throw Exception("Failed to generate order ID")
            orderRef.setValue(order.copy(orderId = orderId)).await()
            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
