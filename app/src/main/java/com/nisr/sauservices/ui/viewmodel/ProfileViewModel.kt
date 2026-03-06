package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profilePicUrl: String? = null,
    val role: String = "customer"
)

data class Address(
    val id: String = "",
    val fullName: String = "",
    val phone: String = "",
    val houseNo: String = "",
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val landmark: String = "",
    val isDefault: Boolean = false
)

data class NotificationPreferences(
    val orderUpdates: Boolean = true,
    val promotions: Boolean = true,
    val serviceAlerts: Boolean = true,
    val appUpdates: Boolean = true
)

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses

    private val _notificationPrefs = MutableStateFlow(NotificationPreferences())
    val notificationPrefs: StateFlow<NotificationPreferences> = _notificationPrefs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchUserProfile()
        fetchAddresses()
        fetchNotificationPreferences()
    }

    fun fetchUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doc = firestore.collection("users").document(uid).get().await()
                if (doc.exists()) {
                    _userProfile.value = UserProfile(
                        name = doc.getString("name") ?: "Guest User",
                        email = doc.getString("email") ?: "guest@example.com",
                        phone = doc.getString("phone") ?: "",
                        profilePicUrl = doc.getString("profilePicUrl"),
                        role = doc.getString("role") ?: "customer"
                    )
                } else {
                    _userProfile.value = UserProfile(name = "Guest User", email = "guest@example.com")
                }
            } catch (e: Exception) {
                _userProfile.value = UserProfile(name = "Guest User", email = "guest@example.com")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(name: String, phone: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                firestore.collection("users").document(uid).update(
                    mapOf("name" to name, "phone" to phone)
                ).await()
                fetchUserProfile()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchAddresses() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("users").document(uid)
                    .collection("addresses").get().await()
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Address::class.java)?.copy(id = doc.id)
                }
                _addresses.value = list
            } catch (e: Exception) { }
        }
    }

    fun addAddress(address: Address) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                firestore.collection("users").document(uid)
                    .collection("addresses").add(address).await()
                fetchAddresses()
            } catch (e: Exception) { }
        }
    }

    fun deleteAddress(addressId: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                firestore.collection("users").document(uid)
                    .collection("addresses").document(addressId).delete().await()
                fetchAddresses()
            } catch (e: Exception) { }
        }
    }

    fun setDefaultAddress(addressId: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val batch = firestore.batch()
                _addresses.value.forEach {
                    val ref = firestore.collection("users").document(uid)
                        .collection("addresses").document(it.id)
                    batch.update(ref, "isDefault", it.id == addressId)
                }
                batch.commit().await()
                fetchAddresses()
            } catch (e: Exception) { }
        }
    }

    fun fetchNotificationPreferences() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val doc = firestore.collection("users").document(uid).get().await()
                val prefs = doc.get("preferences") as? Map<String, Boolean>
                if (prefs != null) {
                    _notificationPrefs.value = NotificationPreferences(
                        orderUpdates = prefs["orderUpdates"] ?: true,
                        promotions = prefs["promotions"] ?: true,
                        serviceAlerts = prefs["serviceAlerts"] ?: true,
                        appUpdates = prefs["appUpdates"] ?: true
                    )
                }
            } catch (e: Exception) { }
        }
    }

    fun updateNotificationPref(key: String, value: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                firestore.collection("users").document(uid).update(
                    "preferences.$key", value
                ).await()
                fetchNotificationPreferences()
            } catch (e: Exception) { }
        }
    }

    fun submitSupportMessage(subject: String, message: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val data = mapOf(
                    "userId" to uid,
                    "subject" to subject,
                    "message" to message,
                    "timestamp" to System.currentTimeMillis()
                )
                firestore.collection("support_messages").add(data).await()
            } catch (e: Exception) { }
        }
    }

    fun logout() {
        auth.signOut()
    }
}
