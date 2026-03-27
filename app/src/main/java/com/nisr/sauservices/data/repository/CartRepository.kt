package com.nisr.sauservices.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.nisr.sauservices.data.model.CartModel
import com.nisr.sauservices.data.model.OrderModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CartRepository {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private fun getUserId(): String = auth.currentUser?.uid ?: "anonymous"
    
    private fun getCartRef(): DatabaseReference {
        val uid = getUserId()
        return database.getReference("cart").child(uid).child("items")
    }

    fun addToCart(item: CartModel) {
        val ref = getCartRef()
        val key = ref.push().key ?: return
        ref.child(key).setValue(item.copy(itemId = key))
    }

    fun updateItem(item: CartModel) {
        if (item.itemId.isNotEmpty()) {
            getCartRef().child(item.itemId).setValue(item)
        }
    }

    /**
     * Observes the cart for the CURRENTLY logged in user.
     * Note: If auth state changes, this flow needs to be re-collected.
     */
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
        
        val ref = getCartRef()
        ref.addValueEventListener(listener)
        
        // Listen for Auth changes to restart or redirect if needed
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            // If user changes, we might want to reload. 
            // In this simple implementation, we just rely on the UI re-collecting 
            // or the ViewModel handling the switch.
        }
        auth.addAuthStateListener(authListener)

        awaitClose { 
            ref.removeEventListener(listener)
            auth.removeAuthStateListener(authListener)
        }
    }

    fun removeItem(itemId: String) {
        getCartRef().child(itemId).removeValue()
    }

    fun clearCart() {
        getCartRef().removeValue()
    }

    suspend fun placeOrder(order: OrderModel): Result<String> {
        return try {
            val uid = getUserId()
            val orderRef = database.getReference("orders").child(uid).push()
            val orderId = orderRef.key ?: throw Exception("Failed to generate order ID")
            orderRef.setValue(order.copy(orderId = orderId)).await()
            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
