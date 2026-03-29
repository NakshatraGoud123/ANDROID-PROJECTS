package com.nisr.sauservices.ui.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nisr.sauservices.ui.viewmodels.ShopkeeperViewModel

@Composable
fun ShopkeeperDashboardMapScreen(
    viewModel: ShopkeeperViewModel,
    deliveryBoyId: String
) {
    val deliveryLocation by viewModel.deliveryBoyLocation.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(deliveryLocation ?: LatLng(0.0, 0.0), 15f)
    }

    LaunchedEffect(deliveryBoyId) {
        viewModel.observeDeliveryBoyLocation(deliveryBoyId)
    }

    LaunchedEffect(deliveryLocation) {
        deliveryLocation?.let {
            cameraPositionState.animate(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLng(it)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false)
        ) {
            deliveryLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Delivery Boy",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }
        }
    }
}
