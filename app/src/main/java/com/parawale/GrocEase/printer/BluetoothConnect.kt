package com.parawale.GrocEase.printer

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.parawale.GrocEase.cartScreen.selectedPrinter

@Suppress("MissingPermission")
@Composable
fun BluetoothScreen() {
    val context = LocalContext.current
    val mDeviceList = remember { mutableStateListOf<BluetoothDevice>() }
    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
    var mExpanded by remember { mutableStateOf(false) }

    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    // Check if Bluetooth is supported on the device
    if (bluetoothAdapter == null) {
        Toast.makeText(context, "Bluetooth is not available", Toast.LENGTH_SHORT).show()
        return
    }

    // Ensuring Bluetooth is enabled using a launcher
    val enableBtLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Bluetooth is enabled
            if (hasBluetoothConnectPermission(context)) {
                discoverDevices(bluetoothAdapter, mDeviceList)
            }
        } else {
            Toast.makeText(context, "Bluetooth not enabled", Toast.LENGTH_SHORT).show()
        }
    }

    // Request Bluetooth permissions launcher
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permissions are granted, proceed with Bluetooth operations
            if (bluetoothAdapter.isEnabled) {
                discoverDevices(bluetoothAdapter, mDeviceList)
            } else {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBtLauncher.launch(enableBtIntent)
            }
        } else {
            Toast.makeText(context, "Bluetooth permissions are required", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                // Request Bluetooth permissions on button click
                permissionsLauncher.launch(Manifest.permission.BLUETOOTH)
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Enable Bluetooth and Discover Devices")
        }

        if (mDeviceList.isNotEmpty()) {
            DropdownMenu(
                expanded = mExpanded,
                onDismissRequest = { mExpanded = false }
            ) {
                mDeviceList.forEach { device ->
                    DropdownMenuItem(onClick = {
                        selectedDevice = device
                        selectedPrinter = device.address
                        mExpanded = false
                    }) {
                        Text(text = device.name)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mExpanded = true }
                    .border(1.dp, Color.Black)
                    .padding(16.dp)
            ) {
                Text(text = selectedPrinter ?: "Select a device")
            }

            Text(
                text = "Selected Printer: ${selectedDevice?.name} \n ${selectedDevice?.address}",
                modifier = Modifier.fillMaxWidth()
            )

            Toast.makeText(
                context,
                "Selected Printer: ${selectedDevice?.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Suppress("MissingPermission")
private fun discoverDevices(bluetoothAdapter: BluetoothAdapter, mDeviceList: MutableList<BluetoothDevice>) {
    val devices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
    mDeviceList.clear()
    mDeviceList.addAll(devices)
}

private fun hasBluetoothConnectPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.BLUETOOTH
    ) == PackageManager.PERMISSION_GRANTED
}
