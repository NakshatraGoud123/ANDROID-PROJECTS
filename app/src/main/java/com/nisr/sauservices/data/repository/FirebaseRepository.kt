package com.nisr.sauservices.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nisr.sauservices.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // --- AUTHENTICATION ---
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun registerUser(user: User): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            db.collection("users").document(uid).set(user.copy(userId = uid)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(uid: String): Result<User?> {
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            Result.success(snapshot.toObject(User::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserOnlineStatus(uid: String, isOnline: Boolean): Result<Unit> {
        return try {
            val status = if (isOnline) "APPROVED" else "OFFLINE" 
            db.collection("users").document(uid).update("status", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- PRODUCTS & SERVICES ---
    suspend fun addProduct(product: Product): Result<Unit> {
        return try {
            val docRef = db.collection("products").document()
            db.collection("products").document(docRef.id).set(product.copy(productId = docRef.id)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getProducts(category: String? = null): Flow<List<Product>> = callbackFlow {
        val query = if (category != null) {
            db.collection("products").whereEqualTo("category", category)
        } else {
            db.collection("products")
        }
        
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val products = snapshot?.toObjects(Product::class.java) ?: emptyList()
            trySend(products)
        }
        awaitClose { listener.remove() }
    }

    // --- ORDERS & BOOKINGS ---
    suspend fun createOrder(order: Order): Result<String> {
        return try {
            val docRef = db.collection("orders").document()
            val finalOrder = order.copy(orderId = docRef.id)
            db.collection("orders").document(docRef.id).set(finalOrder).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- REAL-TIME LISTENERS ---

    fun observeUserOrders(userId: String): Flow<List<Order>> = callbackFlow {
        val listener = db.collection("orders")
            .whereEqualTo("customerId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.toObjects(Order::class.java) ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    fun observeShopOrders(shopId: String): Flow<List<Order>> = callbackFlow {
        val listener = db.collection("orders")
            .whereEqualTo("shopId", shopId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.toObjects(Order::class.java) ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    fun observeWorkerJobs(workerId: String): Flow<List<Order>> = callbackFlow {
        val listener = db.collection("orders")
            .whereEqualTo("workerId", workerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val jobs = snapshot?.toObjects(Order::class.java) ?: emptyList()
                trySend(jobs)
            }
        awaitClose { listener.remove() }
    }

    fun observeDeliveryTasks(partnerId: String): Flow<List<Order>> = callbackFlow {
        val listener = db.collection("orders")
            .whereEqualTo("deliveryPartnerId", partnerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val tasks = snapshot?.toObjects(Order::class.java) ?: emptyList()
                trySend(tasks)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return try {
            db.collection("orders").document(orderId).update("orderStatus", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- ASSIGNMENT ENGINE SIMULATION ---
    suspend fun autoAssignDeliveryPartner(orderId: String): Result<Unit> {
        return try {
            // Find one approved/available delivery partner (simplification)
            val partners = db.collection("users")
                .whereEqualTo("role", "DELIVERY")
                .whereEqualTo("status", "APPROVED")
                .limit(1)
                .get().await()
                .toObjects(User::class.java)

            if (partners.isNotEmpty()) {
                db.collection("orders").document(orderId)
                    .update("deliveryPartnerId", partners[0].userId).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("No delivery partners available"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
