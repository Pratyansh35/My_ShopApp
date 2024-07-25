package com.example.parawaleapp.Notifications

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext


@Composable
fun OrderStatusNotification(title: String, message: String) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Toast.makeText(context, "$title: $message", Toast.LENGTH_LONG).show()
    }
}
