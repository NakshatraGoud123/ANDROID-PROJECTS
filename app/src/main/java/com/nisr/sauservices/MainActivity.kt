package com.nisr.sauservices

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.nisr.sauservices.navigation.AppNavHost
import com.nisr.sauservices.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Realtime Database Connection Test with Region URL
        val databaseUrl = "https://sau-services-default-rtdb.asia-southeast1.firebasedatabase.app/"
        val database = FirebaseDatabase.getInstance(databaseUrl)
        val ref = database.getReference("testNode")
        
        ref.setValue("Connected!")
            .addOnSuccessListener {
                Log.d("FIREBASE_TEST", "Realtime DB Write Success!")
                Toast.makeText(this@MainActivity, "Write Success!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE_TEST", "Realtime DB Write Failed: ${e.message}")
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }

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
                        Log.d("FIREBASE_TEST", "Firestore Connection Successful!")
                        // Already showing toast for Realtime DB
                    }.addOnFailureListener { e ->
                        Log.e("FIREBASE_TEST", "Firestore Connection Failed: ${e.message}")
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
