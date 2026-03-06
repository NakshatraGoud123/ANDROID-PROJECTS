package com.nisr.sauservices.ui.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.card.MaterialCardView
import com.nisr.sauservices.ui.adapters.BookingsAdapter
import com.nisr.sauservices.ui.viewmodel.ServiceWorkerViewModel

class ServiceWorkerDashboardActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewModel: ServiceWorkerViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val role = intent.getStringExtra("ROLE")
        if (role != "service_worker") {
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[ServiceWorkerViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F7F6"))
        }

        val toolbar = TextView(this).apply {
            text = "Service Worker Dashboard"
            textSize = 20f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#2E7D6B"))
            setPadding(48, 48, 48, 48)
            typeface = Typeface.DEFAULT_BOLD
        }
        root.addView(toolbar)

        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
        }
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        // Today's Bookings Count Card
        val countCard = MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            radius = 16 * resources.displayMetrics.density
            cardElevation = 8 * resources.displayMetrics.density
            setCardBackgroundColor(Color.WHITE)
        }
        val countLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            gravity = Gravity.CENTER
        }
        val countTitle = TextView(this).apply { text = "Today's Bookings"; textSize = 14f; setTextColor(Color.GRAY) }
        val countValue = TextView(this).apply { text = "5"; textSize = 24f; setTextColor(Color.parseColor("#2E7D6B")); typeface = Typeface.DEFAULT_BOLD }
        countLayout.addView(countTitle)
        countLayout.addView(countValue)
        countCard.addView(countLayout)
        contentLayout.addView(countCard)

        // Live Location Map
        val mapCard = MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600).apply {
                topMargin = 32
            }
            radius = 16 * resources.displayMetrics.density
            cardElevation = 8 * resources.displayMetrics.density
        }
        val mapFrame = FrameLayout(this).apply { id = View_ID_MAP }
        mapCard.addView(mapFrame)
        contentLayout.addView(mapCard)

        // Bookings List
        val bookingsTitle = TextView(this).apply {
            text = "Bookings"
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 32, 0, 16)
        }
        contentLayout.addView(bookingsTitle)

        val recyclerView = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutManager = LinearLayoutManager(this@ServiceWorkerDashboardActivity)
        }
        val adapter = BookingsAdapter(emptyList()) { booking ->
            viewModel.updateBookingStatus(booking.bookingId, booking.status)
        }
        recyclerView.adapter = adapter
        contentLayout.addView(recyclerView)

        viewModel.bookings.observe(this) { bookings ->
            adapter.updateData(bookings)
        }

        scrollView.addView(contentLayout)
        root.addView(scrollView)
        setContentView(root)

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().add(View_ID_MAP, mapFragment).commit()
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        checkLocationPermission()
        
        // Mock customer marker
        val mockCustomer = LatLng(12.9716, 77.5946) // Bangalore example
        googleMap?.addMarker(MarkerOptions()
            .position(mockCustomer)
            .title("Customer Location")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap?.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    googleMap?.addMarker(MarkerOptions().position(latLng).title("My Location"))
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                }
            }
        }
    }

    companion object {
        private const val View_ID_MAP = 20001
    }
}
