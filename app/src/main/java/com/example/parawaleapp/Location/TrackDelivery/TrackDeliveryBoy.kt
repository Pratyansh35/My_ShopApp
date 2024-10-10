package com.example.parawaleapp.Location.TrackDelivery

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun TrackDeliveryBoyScreen(deliveryBoyId: String, destinationLatLng: LatLng) {
    val context = LocalContext.current
    val database: DatabaseReference = Firebase.database.reference
    var deliveryBoyLocation by remember { mutableStateOf<LatLng?>(null) }
    var estimatedTime by remember { mutableStateOf("Calculating...") }

    // Camera state to control the map
    val cameraPositionState = rememberCameraPositionState()

    // Listen for delivery boy's location updates
    LaunchedEffect(deliveryBoyId) {
        val deliveryBoyLocationRef = database.child("deliveryBoys").child(deliveryBoyId)
        deliveryBoyLocationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val locationMap = snapshot.value as? Map<*, *>
                locationMap?.let {
                    val lat = locationMap["latitude"] as Double
                    val lng = locationMap["longitude"] as Double
                    val latLng = LatLng(lat, lng)
                    deliveryBoyLocation = latLng
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 16f)

                    // Calculate the estimated time
                    val distance = calculateDistance(latLng, destinationLatLng)
                    estimatedTime = calculateEstimatedTime(distance)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching location", error.toException())
            }
        })
    }

    // UI to display the map and marker for delivery boy
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
        ) {
            deliveryBoyLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Delivery Boy Location"
                )
            }
        }

        // Displaying the estimated time and action buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Estimated Time to Arrival: $estimatedTime",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Button(
                    onClick = {
                        // Call the delivery boy
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+123456789"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Call Delivery Boy")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        // Chat with the delivery boy
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Chat with Delivery Boy")
                }
            }
        }
    }
}