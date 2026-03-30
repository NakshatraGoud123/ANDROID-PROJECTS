package com.nisr.sauservices.ui.dashboard

import android.Manifest
import android.content.Intent
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.card.MaterialCardView
import com.nisr.sauservices.data.model.Delivery
import com.nisr.sauservices.ui.adapters.DeliveryAdapter
import com.nisr.sauservices.ui.viewmodels.DeliveryBoyViewModel
import kotlinx.coroutines.launch

class DeliveryDashboardActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewModel: DeliveryBoyViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null
    private var myMarker: Marker? = null
    private val customerMarkers = mutableMapOf<String, Marker>()
    private val LOCATION_PERMISSION_REQUEST_CODE = 1003

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val role = intent.getStringExtra("ROLE")
        if (role != "delivery") {
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[DeliveryBoyViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F7F6"))
        }

        val toolbar = TextView(this).apply {
            text = "Delivery Dashboard"
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

        // Summary Card
        val summaryLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 2f
        }
        summaryLayout.addView(createSummaryCard("Assigned", "0", 1f))
        summaryLayout.addView(createSummaryCard("Earnings", "₹0", 1f))
        contentLayout.addView(summaryLayout)

        // Map Section
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

        // Deliveries List
        val deliveriesTitle = TextView(this).apply {
            text = "Active Deliveries"
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 32, 0, 16)
        }
        contentLayout.addView(deliveriesTitle)

        val recyclerView = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutManager = LinearLayoutManager(this@DeliveryDashboardActivity)
        }
        val adapter = DeliveryAdapter(emptyList()) { delivery ->
            if (delivery.status == "delivered") {
                viewModel.markDelivered(delivery.deliveryId)
            } else if (delivery.status == "assigned") {
                viewModel.startDelivery(this@DeliveryDashboardActivity, delivery.deliveryId)
            }
        }
        recyclerView.adapter = adapter
        contentLayout.addView(recyclerView)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.assignedOrders.collect { orderModels ->
                    val deliveries = orderModels.map { 
                        Delivery(
                            deliveryId = it.orderId,
                            customerName = "Customer",
                            pickupAddress = "Shop",
                            dropAddress = it.address,
                            distance = "Calculating...",
                            status = it.orderStatus,
                            items = ""
                        )
                    }
                    adapter.updateData(deliveries)
                    updateCustomerMarkers(orderModels)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentLocation.collect { latLng ->
                    latLng?.let {
                        if (myMarker == null) {
                            myMarker = googleMap?.addMarker(MarkerOptions()
                                .position(it)
                                .title("My Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                        } else {
                            myMarker?.position = it
                        }
                    }
                }
            }
        }

        scrollView.addView(contentLayout)
        root.addView(scrollView)
        setContentView(root)

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().add(View_ID_MAP, mapFragment).commit()
        mapFragment.getMapAsync(this)
    }

    private fun updateCustomerMarkers(orders: List<com.nisr.sauservices.data.model.OrderModel>) {
        googleMap?.let { map ->
            // Clear markers not in current orders
            val orderIds = orders.map { it.orderId }.toSet()
            val iterator = customerMarkers.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (entry.key !in orderIds) {
                    entry.value.remove()
                    iterator.remove()
                }
            }

            // Add or update markers for current orders
            orders.forEach { order ->
                val pos = LatLng(order.customerLocation.lat, order.customerLocation.lng)
                if (pos.latitude != 0.0) {
                    if (customerMarkers.containsKey(order.orderId)) {
                        customerMarkers[order.orderId]?.position = pos
                    } else {
                        val marker = map.addMarker(MarkerOptions()
                            .position(pos)
                            .title("Customer: ${order.orderId}")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                        marker?.let { customerMarkers[order.orderId] = it }
                    }
                }
            }
        }
    }

    private fun createSummaryCard(title: String, value: String, weight: Float): MaterialCardView {
        val card = MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight).apply {
                setMargins(8, 0, 8, 0)
            }
            radius = 16 * resources.displayMetrics.density
            cardElevation = 4 * resources.displayMetrics.density
            setCardBackgroundColor(Color.WHITE)
        }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 24, 16, 24)
            gravity = Gravity.CENTER
        }
        val titleText = TextView(this).apply { text = title; textSize = 12f; setTextColor(Color.GRAY); gravity = Gravity.CENTER }
        val valueText = TextView(this).apply { text = value; textSize = 18f; setTextColor(Color.BLACK); typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER }
        layout.addView(titleText)
        layout.addView(valueText)
        card.addView(layout)
        return card
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        checkLocationPermission()
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
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
                    viewModel.updateLocation(it.latitude, it.longitude)
                }
            }
        }
    }

    companion object {
        private const val View_ID_MAP = 30001
    }
}
