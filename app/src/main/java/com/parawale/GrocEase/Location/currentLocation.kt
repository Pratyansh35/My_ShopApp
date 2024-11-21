package com.parawale.GrocEase.Location


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import java.util.Locale


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun CurrentLocationComposable(
    context: Context,
    onLocationFetched: (String) -> Unit,
    navController: NavController
) {
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var formattedAddress by remember { mutableStateOf("Fetching address...") }
    val geocoder = Geocoder(context, Locale.getDefault())

    LaunchedEffect(key1 = locationPermissionState.allPermissionsGranted) {
        if (locationPermissionState.allPermissionsGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val address = geocoder.getFromLocation(it.latitude, it.longitude, 1)?.firstOrNull()
                    formattedAddress = address?.let {
                        "${it.locality ?: "Unknown locality"}, ${it.adminArea ?: "Unknown admin area"}, " +
                                "${it.countryName ?: "Unknown country"}, ${it.postalCode ?: "Unknown postal code"}"
                    } ?: "Address not found"

                    onLocationFetched(formattedAddress) // Call the callback to pass location data
                }
            }
        } else {
            locationPermissionState.launchMultiplePermissionRequest()
        }
    }

    Text(
        text = formattedAddress,
        style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(16.dp).clickable {
            navController.navigate("mapScreen")
        }
    )
}

@SuppressLint("MissingPermission")
@Composable
fun FetchCurrentLocation(
    context: Context,
    onLocationFetched: (Location) -> Unit
) {
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                onLocationFetched(it)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationCard(
    icon: ImageVector,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var formattedAddress by remember { mutableStateOf("Fetching address...") }
    val geocoder = Geocoder(context, Locale.getDefault())

    LaunchedEffect(key1 = locationPermissionState.allPermissionsGranted) {
        if (locationPermissionState.allPermissionsGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val address = geocoder.getFromLocation(it.latitude, it.longitude, 1)?.firstOrNull()
                    formattedAddress = address?.let {
                        "${it.locality ?: "Unknown locality"}, ${it.adminArea ?: "Unknown admin area"}, " +
                                "${it.countryName ?: "Unknown country"}, ${it.postalCode ?: "Unknown postal code"}"
                    } ?: "Address not found"
                }
            }
        } else {
            locationPermissionState.launchMultiplePermissionRequest()
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.Green)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Use current location", style = MaterialTheme.typography.body1)
                Text(formattedAddress, style = MaterialTheme.typography.body2)
            }
        }
    }
}

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Search for area, street name...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    )
}
