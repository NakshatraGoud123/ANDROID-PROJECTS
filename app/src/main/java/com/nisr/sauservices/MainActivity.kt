package com.nisr.sauservices

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.nisr.sauservices.navigation.AppNavHost
import com.nisr.sauservices.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current

            // Verification Block: Test Firestore Connection
            LaunchedEffect(Unit) {
                try {
                    val db = FirebaseFirestore.getInstance()
                    val testRef = db.collection("connection_test").document("check")
                    
                    testRef.set(mapOf(
                        "status" to "Backend Active",
                        "last_checked" to System.currentTimeMillis()
                    )).addOnSuccessListener {
                        Log.d("FIREBASE_TEST", "Connection Successful!")
                        Toast.makeText(context, "Firebase Connected!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e ->
                        Log.e("FIREBASE_TEST", "Connection Failed: ${e.message}")
                        // If this fails with "Permission Denied", update your rules to allow 'connection_test' collection
                    }
                } catch (e: Exception) {
                    Log.e("FIREBASE_TEST", "Init Error: ${e.message}")
                }
            }

            AppTheme {
                AppNavHost(navController)
            }
        }
    }
}
