package com.parawale.GrocEase.PermissionHandling

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionHandler(
    permissions: List<String> = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ),
    onPermissionsGranted: () -> Unit,
    onPermissionsDenied: () -> Unit
) {
    val locationPermissionState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(key1 = locationPermissionState.allPermissionsGranted) {
        if (locationPermissionState.allPermissionsGranted) {
            onPermissionsGranted()
        } else {
            locationPermissionState.launchMultiplePermissionRequest()
            onPermissionsDenied()
        }
    }
}


