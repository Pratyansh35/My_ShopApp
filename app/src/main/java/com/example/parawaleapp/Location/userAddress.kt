package com.example.parawaleapp.Location

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState


import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import fetchDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun UserAddressScreen(onConfirm: (LatLng, String) -> Unit) {
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var formattedAddress by remember { mutableStateOf("Fetching address...") }

    val geocoder = Geocoder(context, Locale.getDefault())

    // Create a state for the camera position and set it to null initially
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(key1 = locationPermissionState.allPermissionsGranted) {
        if (locationPermissionState.allPermissionsGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentLocation = latLng
                    selectedLocation = latLng

                    // Update camera position when the location is fetched
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 14f)

                    // Fetch and update formatted address
                    val address = geocoder.getFromLocation(it.latitude, it.longitude, 1)?.firstOrNull()
                    formattedAddress = address?.let {
                        "${it.locality}, ${it.adminArea}, ${it.countryName}, ${it.postalCode}"
                    } ?: "Address not found"
                }
            }
        } else {
            locationPermissionState.launchMultiplePermissionRequest()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (selectedLocation != null) {
            GoogleMap(
                modifier = Modifier.weight(1f),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(position = selectedLocation!!),
                    title = "Selected Location",
                    draggable = true,
                    onClick = { true }
                )

                // Update selectedLocation and formatted address when marker is dragged
                LaunchedEffect(key1 = selectedLocation) {
                    selectedLocation?.let {
                        val address = geocoder.getFromLocation(
                            it.latitude,
                            it.longitude,
                            1
                        )?.firstOrNull()

                        formattedAddress = address?.let {
                            "${it.locality}, ${it.adminArea}, ${it.countryName}, ${it.postalCode}"
                        } ?: "Address not found"
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display formatted address
            Text(
                text = formattedAddress,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm button
            Button(
                onClick = {
                    selectedLocation?.let {
                        onConfirm(it, formattedAddress)
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Confirm Address")
            }
        } else {
            // Show loading text while fetching location
            Text(
                text = "Fetching current location...",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}


@Composable
fun AdditionalDetailsScreen(
    home: String,
    apartment: String,
    landmark: String,
    notes: String,
    onSave: (String, String, String, String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = home,
            onValueChange = { onSave(it, apartment, landmark, notes) },
            label = { Text("Home") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = apartment,
            onValueChange = { onSave(home, it, landmark, notes) },
            label = { Text("Apartment") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = landmark,
            onValueChange = { onSave(home, apartment, it, notes) },
            label = { Text("Landmark") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { onSave(home, apartment, landmark, it) },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
