package com.nisr.sauservices.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("sau_services_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_LOGIN_STATE = "login_state"
        const val KEY_USER_ROLE = "user_role"
        const val KEY_WORKER_TYPE = "worker_type"
        const val KEY_LAT = "latitude"
        const val KEY_LNG = "longitude"
        const val KEY_ADDRESS = "address"
    }

    fun saveLoginState(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_LOGIN_STATE, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGIN_STATE, false)

    fun saveUserRole(role: String) {
        prefs.edit().putString(KEY_USER_ROLE, role).apply()
    }

    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, null)

    fun saveWorkerType(type: String) {
        prefs.edit().putString(KEY_WORKER_TYPE, type).apply()
    }

    fun getWorkerType(): String? = prefs.getString(KEY_WORKER_TYPE, null)

    fun saveLocation(lat: Double, lng: Double, address: String) {
        prefs.edit().apply {
            putFloat(KEY_LAT, lat.toFloat())
            putFloat(KEY_LNG, lng.toFloat())
            putString(KEY_ADDRESS, address)
        }.apply()
    }

    fun getAddress(): String = prefs.getString(KEY_ADDRESS, "Fetching location...") ?: "Fetching location..."

    fun logout() {
        prefs.edit().clear().apply()
    }
}
