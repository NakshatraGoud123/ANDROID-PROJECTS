package com.nisr.sauservices.ui.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.nisr.sauservices.ui.adapters.OrdersAdapter
import com.nisr.sauservices.ui.viewmodel.ShopkeeperViewModel

class ShopkeeperDashboardActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewModel: ShopkeeperViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val role = intent.getStringExtra("ROLE")
        if (role != "shopkeeper") {
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[ShopkeeperViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F7F6"))
        }

        val toolbar = TextView(this).apply {
            text = "Shopkeeper Dashboard"
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

        // Summary Cards
        val summaryLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 3f
        }
        summaryLayout.addView(createSummaryCard("Total Products", "120", 1f))
        summaryLayout.addView(createSummaryCard("Today Orders", "15", 1f))
        summaryLayout.addView(createSummaryCard("Revenue", "$1.2k", 1f))
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

        val updateLocBtn = MaterialButton(this).apply {
            text = "Update Location"
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = 16
            }
            cornerRadius = (24 * resources.displayMetrics.density).toInt()
            setBackgroundColor(Color.parseColor("#2E7D6B"))
        }
        updateLocBtn.setOnClickListener { checkLocationPermission() }
        contentLayout.addView(updateLocBtn)

        // Orders List
        val ordersTitle = TextView(this).apply {
            text = "Orders"
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 32, 0, 16)
        }
        contentLayout.addView(ordersTitle)

        val recyclerView = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutManager = LinearLayoutManager(this@ShopkeeperDashboardActivity)
        }
        val adapter = OrdersAdapter(emptyList()) { order ->
            viewModel.updateOrderStatus(order, order.status)
        }
        recyclerView.adapter = adapter
        contentLayout.addView(recyclerView)

        viewModel.orders.observe(this) { orders ->
            adapter.updateData(orders)
        }

        scrollView.addView(contentLayout)
        root.addView(scrollView)
        setContentView(root)

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().add(View_ID_MAP, mapFragment).commit()
        mapFragment.getMapAsync(this)
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
            gravity = android.view.Gravity.CENTER
        }
        val titleText = TextView(this).apply { text = title; textSize = 12f; setTextColor(Color.GRAY); gravity = android.view.Gravity.CENTER }
        val valueText = TextView(this).apply { text = value; textSize = 16f; setTextColor(Color.BLACK); typeface = Typeface.DEFAULT_BOLD; gravity = android.view.Gravity.CENTER }
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
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    googleMap?.addMarker(MarkerOptions().position(latLng).title("Shop Location"))
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
        }
    }

    companion object {
        private const val View_ID_MAP = 10001
    }
}
