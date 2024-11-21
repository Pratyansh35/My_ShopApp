package com.parawale.GrocEase.barcodeScreen

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.parawale.GrocEase.DataClasses.Dishfordb

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors


@Composable
fun BarCodeScreen(
    DishData: List<Dishfordb>,
    cartItems: MutableList<Dishfordb>,
    updateTotals: () -> Unit,
    //saveCartItemsToSharedPreferences: () -> Unit
) {
    var showItemScreen by remember { mutableStateOf(false) }
    var gotDish by remember { mutableStateOf<Dishfordb?>(null) }
    if (showItemScreen && gotDish != null) {
        ItemScreen(
            dish = gotDish!!,
            showItemScreen = { showItemScreen = false },
            cartItems = cartItems,
            updateTotals = updateTotals,
            //saveCartItemsToSharedPreferences = saveCartItemsToSharedPreferences
        )
    } else {
        barCodeScreen(showItemScreen = {
            showItemScreen = true
            gotDish = it
        }, DishData = DishData)
    }
}


@Composable
@androidx.annotation.OptIn(ExperimentalGetImage::class)
fun barCodeScreen(showItemScreen: (Dishfordb) -> Unit, DishData: List<Dishfordb>) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val scannedBarcode = remember { mutableStateOf<String?>(null) }
    RequestCameraPermission()
    DisposableEffect(lifecycleOwner) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView }, modifier = Modifier.fillMaxSize()
        ) {
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
                        .also { analysis ->
                            analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                val mediaImage = imageProxy.image
                                if (mediaImage != null) {
                                    val image = InputImage.fromMediaImage(
                                        mediaImage, imageProxy.imageInfo.rotationDegrees
                                    )
                                    val scanner: BarcodeScanner = BarcodeScanning.getClient()
                                    scanner.process(image).addOnSuccessListener { barcodes ->
                                        for (barcode in barcodes) {
                                            val rawValue = barcode.rawValue
                                            if (rawValue != null) {
                                                scannedBarcode.value = rawValue
                                            }
                                            Log.e("BarcodeScanner", "Barcode detected: $rawValue")
                                        }
                                    }.addOnFailureListener { e ->
                                        Log.e(
                                            "BarcodeScanner", "Barcode scanning failed", e
                                        )
                                    }.addOnCompleteListener {
                                        imageProxy.close()
                                    }
                                } else {
                                    imageProxy.close()
                                }
                            }
                        }

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageAnalysis
                    )
                } catch (e: Exception) {
                    Log.e("BarCodeScreen", "Failed to bind camera use cases", e)
                }
            }, ContextCompat.getMainExecutor(context))
        }

        // Overlay with transparent center
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000)) // semi-transparent overlay
        ) {
            Box(
                modifier = Modifier
                    .size(250.dp, 150.dp) // size of the central rectangle
                    .align(Alignment.Center)
                    .background(Color.Transparent)
                    .border(2.dp, Color.White) // white border for the rectangle
            )
        }
    }

    scannedBarcode.value?.let { barcode ->
        val gotDish = DishData.find { it.barcode == barcode }
        if (gotDish != null) {
            Toast.makeText(context, "Barcode detected: $barcode", Toast.LENGTH_SHORT).show()
            showItemScreen(gotDish)
        } else {
            Toast.makeText(
                context, "No item found $barcode", Toast.LENGTH_SHORT

            ).show()
        }
        // Reset scannedBarcode to prevent repeated actions for the same barcode
        scannedBarcode.value = null
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestCameraPermission() {
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        permissionState.launchPermissionRequest()
    }

    when {
        permissionState.status.isGranted -> {
            Toast.makeText(context, "Camera permission granted", Toast.LENGTH_SHORT).show()
        }

        permissionState.status.shouldShowRationale -> {
            Toast.makeText(
                context, "Camera permission is needed to scan barcodes", Toast.LENGTH_SHORT
            ).show()

        }

        !permissionState.status.isGranted -> {
            Toast.makeText(
                context, "Camera permission denied. Cannot proceed further.", Toast.LENGTH_SHORT
            ).show()
        }
    }
}
