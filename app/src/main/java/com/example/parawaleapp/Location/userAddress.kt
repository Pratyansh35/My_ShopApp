package com.example.parawaleapp.Location


import Home
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parawaleapp.DataClasses.UserAddressDetails
import com.example.parawaleapp.DataClasses.UserData
import com.example.parawaleapp.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale
import java.util.Random



@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun fetchAddress(
    context: Context,
    latitude: Double,
    longitude: Double,
    formattedAddress: MutableState<String>,
    locality: MutableState<String>,
    city: MutableState<String>,
    state: MutableState<String>,
    postalCode: MutableState<String>,
    onComplete: () -> Unit
) {
    val geocoder = Geocoder(context, Locale.getDefault())
    geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
        override fun onGeocode(addresses: MutableList<Address>) {
            if (addresses.isEmpty()) {
                formattedAddress.value = "Address not found"
            } else {
                val address = addresses.firstOrNull()
                formattedAddress.value = "${address?.subThoroughfare ?: ""} ${address?.thoroughfare ?: ""} ${address?.locality ?: ""}, ${address?.adminArea ?: ""}, ${address?.postalCode ?: ""}"
                locality.value = "${address?.subThoroughfare ?: ""} ${address?.thoroughfare ?: ""}".trim()
                city.value = address?.locality ?: ""
                state.value = address?.adminArea ?: ""
                postalCode.value = address?.postalCode ?: ""
            }
            onComplete()
        }

        override fun onError(errorMessage: String?) {
            formattedAddress.value = "Error: $errorMessage"
            onComplete()
        }
    })
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun UserAddressScreen(
    navController: NavController,
    onMapInteractionStart: () -> Unit,
    onMapInteractionEnd: () -> Unit,
) {
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
    val formattedAddress = remember { mutableStateOf("Fetching address...") }
    val locality = remember { mutableStateOf("") }
    val city = remember { mutableStateOf("") }
    val state = remember { mutableStateOf("") }
    val postalCode = remember { mutableStateOf("") }

    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

    // Camera state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 2f)
    }
    val gson = Gson()

    LaunchedEffect(locationPermissionState.allPermissionsGranted) {
        if (locationPermissionState.allPermissionsGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentLocation = latLng
                    selectedLocation = latLng
                    latitude = latLng.latitude.toString()
                    longitude = latLng.longitude.toString()

                    // Move camera to current location
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 16f)

                    // Fetch address asynchronously
                    fetchAddress(context, latLng.latitude, latLng.longitude, formattedAddress, locality, city, state, postalCode) {
                        // Address fetch completed
                    }
                }
            }
        } else {
            locationPermissionState.launchMultiplePermissionRequest()
        }
    }

    // Handle map idle to update location details
    var lastKnownPosition by remember { mutableStateOf<LatLng?>(null) }
    var isAddressFetching by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = cameraPositionState.position) {
        val centerPosition = cameraPositionState.position.target

        // Check if the center position has changed significantly
        if (lastKnownPosition == null ||
            lastKnownPosition!!.latitude != centerPosition.latitude ||
            lastKnownPosition!!.longitude != centerPosition.longitude) {
            lastKnownPosition = centerPosition

            // Prevent multiple fetches
            if (!isAddressFetching) {
                isAddressFetching = true
                fetchAddress(context, centerPosition.latitude, centerPosition.longitude, formattedAddress, locality, city, state, postalCode) {
                    isAddressFetching = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.68f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            onMapInteractionStart()
                            tryAwaitRelease()
                        },
                        onTap = {
                            onMapInteractionStart()
                        }
                    )
                }
        ) {
            // Google Map
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = {
                    onMapInteractionStart()
                }
            )

            // Fixed marker at 50% from top
            Icon(
                painter = painterResource(id = R.drawable.map_marker),
                contentDescription = "Marker",
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-32).dp),
                tint = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            elevation = 4.dp,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "DELIVERING AT",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.error
                )

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.map_marker),
                        contentDescription = "Marker",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formattedAddress.value,
                        maxLines = 2,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        onMapInteractionEnd()
                        val userAddressDetails = UserAddressDetails(
                            LocId = Random().nextInt(1000).toString(),
                            name = "",
                            phone = "",
                            pincode = postalCode.value,
                            address = locality.value.ifEmpty { "" },
                            landmark = "",
                            city = city.value,
                            state = state.value,
                            latitude = latitude,
                            longitude = longitude,
                            isHome = true,
                            isWork = false,
                            isDefault = true
                        )
                        val userAddressJson = Uri.encode(gson.toJson(userAddressDetails))
                        navController.navigate("confirmAddress/$userAddressJson")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                ) {
                    Text(
                        text = "Add more details",
                        color = Color.White,
                        style = MaterialTheme.typography.button
                    )
                }
            }
        }
    }
}







@Composable
fun AdditionalDetailsScreen(
    userData: UserData?,
    userAddressDetails: UserAddressDetails,
    onSave: (UserData) -> Unit,
    navController: NavController,
    selectedLocation: (UserAddressDetails) -> Unit
) {
    var name by remember { mutableStateOf(userData?.userName ?: "") }
    var phone by remember { mutableStateOf(userData?.userPhoneNumber ?: "") }
    var pincode by remember { mutableStateOf(userAddressDetails.pincode) }
    var address by remember { mutableStateOf("${userAddressDetails.address} ${userAddressDetails.city}") }
    var landmark by remember { mutableStateOf(userAddressDetails.landmark) }
    var state by remember { mutableStateOf(userAddressDetails.state) }
    var isHome by remember { mutableStateOf(userAddressDetails.isHome) }
    var isWork by remember { mutableStateOf(userAddressDetails.isWork) }
    var isDefault by remember { mutableStateOf(userAddressDetails.isDefault) }

    var recieverEdit by remember { mutableStateOf(false) }
    var infoEdit by remember { mutableStateOf(false) }
    if (phone == "") {
        recieverEdit = true
    }
    // Main column container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Enter Complete Address",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Receiver details card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (!recieverEdit) {
                    Row{
                        Text(
                            text = "Receiver details for this address",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                        TextButton(
                            onClick = { recieverEdit = true },
                            modifier = Modifier.padding(start = 8.dp).align(alignment = Alignment.CenterVertically)
                        ) {
                            Text(text = "Edit")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$name, $phone",
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.primary
                    )
                }else{
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Address Type
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Save address as *", style = MaterialTheme.typography.body1)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    AddressTypeButton(
                        isSelected = isHome,
                        label = "Home",
                        onClick = { isHome = true; isWork = false },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AddressTypeButton(
                        isSelected = isWork,
                        label = "Work",
                        onClick = { isWork = true; isHome = false },
                        modifier = Modifier.weight(1f) // Apply weight correctly inside RowScope
                    )
                }

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Address form inputs
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (!infoEdit) {
                    Row{
                        Text(
                            text = "Info from Maps",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                        TextButton(
                            onClick = { infoEdit = true },
                            modifier = Modifier.padding(start = 8.dp).align(alignment = Alignment.CenterVertically)
                        ) {
                            Text(text = "Edit")
                        }
                    }
                    Text(
                        text = listOfNotNull(
                            address.takeIf { it.isNotEmpty() && it != "null" },
                            state.takeIf { it.isNotEmpty() && it != "null" },
                            pincode.takeIf { it.isNotEmpty() && it != "null" }
                        ).joinToString(", "),
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.primary
                    )

                }else {
                    Text(
                        text = "Info from Maps",
                        style = MaterialTheme.typography.body1
                    )
                    OutlinedTextField(
                        value = pincode,
                        onValueChange = { pincode = it },
                        label = { Text("Pin Code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text("State") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Required Fields",
                    style = MaterialTheme.typography.body1
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = landmark,
                    onValueChange = { landmark = it },
                    label = { Text("(Flat, House No., Building, Company)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        // Set as default option
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isDefault, onCheckedChange = { isDefault = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colors.primary,
                    checkmarkColor = Color.White,
                    uncheckedColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.width(2.dp))
            TextButton (
                onClick = { isDefault = !isDefault },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
            ){
                Text(text = "Set as default address")
            }



        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = {
                val newAddress = UserAddressDetails(
                    LocId = Random().nextInt(1000).toString(),
                    name = name,
                    phone = phone,
                    pincode = pincode,
                    address = address,
                    landmark = landmark,
                    city = userAddressDetails.city,
                    state = state,
                    isHome = isHome,
                    isWork = isWork,
                    isDefault = isDefault,
                    longitude = userAddressDetails.longitude,
                    latitude = userAddressDetails.latitude
                )

                val updatedAddressList = userData?.userAddressDetails?.map {
                    it.copy(isDefault = if (isDefault) false else it.isDefault)
                }?.toMutableList() ?: mutableListOf()

                updatedAddressList.add(newAddress)
                selectedLocation(newAddress)

                val updatedUserData = userData?.copy(
                    userAddressDetails = updatedAddressList,
                    defaultAddress = if (isDefault) newAddress else userData.defaultAddress
                )

                updatedUserData?.let { onSave(it) }
                navController.navigate(Home.route)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Confirm Address", style = MaterialTheme.typography.button)
        }
    }
}

@Composable
fun AddressTypeButton(
    isSelected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .border(1.dp, if (isSelected) MaterialTheme.colors.primary else Color.Gray, shape = RoundedCornerShape(10.dp)),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.2f) else Color.Transparent
        )
    ) {
        Text(text = label, color = if (isSelected) MaterialTheme.colors.primary else Color.Gray)
    }
}




