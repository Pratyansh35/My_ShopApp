import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.parawale.GrocEase.DataClasses.UserAddressDetails
import com.parawale.GrocEase.DataClasses.UserData
import com.parawale.GrocEase.Location.LocationCard
import com.parawale.GrocEase.Location.SearchBar
import com.parawale.GrocEase.R
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL


@Composable
fun LocationSelectionScreen(
    userData: UserData?,
    currentLocation: Location?,
    onCurrentLocationClicked: () -> Unit,
    onAddAddressClicked: () -> Unit,
    onAddressSelected: (UserAddressDetails) -> Unit,
    onEditAddressClicked: (UserAddressDetails) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select a location",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Search Bar
        SearchBar(value = "", onValueChange = {})

        LazyColumn {
            // Use Current Location
            item {
                LocationCard(
                    icon = Icons.Default.LocationOn,
                    onClick = { onCurrentLocationClicked() }
                )


                // Add Address
                TextButton(
                    onClick = { onAddAddressClicked() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Green)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Address")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Address")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Saved Addresses
                Text(
                    text = "SAVED ADDRESSES",
                    style = MaterialTheme.typography.body2.copy(color = Color.Gray),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(userData?.userAddressDetails ?: emptyList()) { address ->
                SavedAddressItem(
                    address = address,
                    onAddressSelected = { onAddressSelected(address) },
                    onEditAddressClicked = { onEditAddressClicked(address) }
                )
            }
        }
    }
}

@Composable
fun SavedAddressItem(
    address: UserAddressDetails,
    onAddressSelected: () -> Unit,
    onEditAddressClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAddressSelected() }
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
                Icon(
                    painter = painterResource(id = if (address.isHome) R.drawable.ic_home2 else R.drawable.work),
                    contentDescription = if (address.isHome) "Home Address" else "Work Address"
                )

            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row {
                    Text(
                        text = if (address.isHome) "Home" else "Work",
                        style = MaterialTheme.typography.body1
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${address.landmark.take(10)}...)",
                        style = MaterialTheme.typography.body1,
                        minLines = 1,

                        )
                }
                Text(
                    text = "${address.address}, ${address.city}, ${address.state}",
                    style = MaterialTheme.typography.body2
                )
                Text(
                    text = "Phone number: ${address.phone}",
                    style = MaterialTheme.typography.caption
                )
            }
            IconButton(onClick = { onEditAddressClicked() }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Edit Address")
            }
        }
    }
}



//@OptIn(ExperimentalPermissionsApi::class)
//@SuppressLint("MissingPermission")
//@Composable
//fun ShopDistanceScreen(shopLocation: LatLng ) {
//    val context = LocalContext.current
//    val fusedLocationClient: FusedLocationProviderClient = remember {
//        LocationServices.getFusedLocationProviderClient(context)
//    }
//    val locationPermissionState = rememberMultiplePermissionsState(
//        permissions = listOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        )
//    )
//
//    var currentLocation by remember { mutableStateOf<Location?>(null) }
//    var polylinePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
//
//    LaunchedEffect(key1 = locationPermissionState.allPermissionsGranted) {
//        if (locationPermissionState.allPermissionsGranted) {
//            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                currentLocation = location
//
//                // Fetch directions and update polyline points
//                CoroutineScope(Dispatchers.IO).launch {
//                    val points = fetchDirections(
//                        LatLng(location.latitude, location.longitude),
//                        shopLocation
//                    )
//                    withContext(Dispatchers.Main) {
//                        polylinePoints = points
//                    }
//                }
//            }
//        } else {
//            locationPermissionState.launchMultiplePermissionRequest()
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        if (currentLocation != null) {
//            val cameraPositionState = rememberCameraPositionState {
//                position = CameraPosition.fromLatLngZoom(
//                    LatLng(currentLocation!!.latitude, currentLocation!!.longitude), 14f
//                )
//            }
//
//            GoogleMap(
//                modifier = Modifier.fillMaxSize(),
//                cameraPositionState = cameraPositionState
//            ) {
//                Marker(
//                    state = MarkerState(position = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)),
//                    title = "Your Location"
//                )
//                Marker(
//                    state = MarkerState(position = shopLocation),
//                    title = "Shop Location"
//                )
//
//                if (polylinePoints.isNotEmpty()) {
//                    Polyline(points = polylinePoints)
//                }
//            }
//        } else {
//            Text(text = "Fetching location...", fontSize = 18.sp)
//        }
//    }
//}

//suspend fun fetchDirections(
//    origin: LatLng,
//    destination: LatLng
//): List<LatLng> {
//    val apiKey = "AIzaSyDXijvfPdIz2sX2_XB-uG2SFUgwDSTwvLY"
//    val url = "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${destination.latitude},${destination.longitude}&key=$apiKey"
//    val response = withContext(Dispatchers.IO) { URL(url).readText() }
//    val jsonResponse = JSONObject(response)
//    val routes = jsonResponse.getJSONArray("routes")
//    return if (routes.length() > 0) {
//        val points = routes.getJSONObject(0)
//            .getJSONObject("overview_polyline")
//            .getString("points")
//        decodePoly(points)
//    } else {
//        emptyList()
//    }
//}
//
//fun decodePoly(encoded: String): List<LatLng> {
//    val poly = ArrayList<LatLng>()
//    var index = 0
//    val len = encoded.length
//    var lat = 0
//    var lng = 0
//
//    while (index < len) {
//        var b: Int
//        var shift = 0
//        var result = 0
//        do {
//            b = encoded[index++].code - 63
//            result = result or (b and 0x1f shl shift)
//            shift += 5
//        } while (b >= 0x20)
//        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
//        lat += dlat
//
//        shift = 0
//        result = 0
//        do {
//            b = encoded[index++].code - 63
//            result = result or (b and 0x1f shl shift)
//            shift += 5
//        } while (b >= 0x20)
//        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
//        lng += dlng
//
//        val p = LatLng(lat / 1E5, lng / 1E5)
//        poly.add(p)
//    }
//
//    return poly
//}
