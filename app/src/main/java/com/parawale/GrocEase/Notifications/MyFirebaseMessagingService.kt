package com.parawale.GrocEase.Notifications

import android.util.Log
import com.parawale.GrocEase.database.datareference
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.parawale.GrocEase.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "HIGH_PRIORITY_CHANNEL"
        val channelName = "High Priority Notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "This channel is used for high priority notifications."
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.notification?.title ?: "Notification"
        val message = remoteMessage.notification?.body ?: "You have a new message."

        // Show notification
        showNotification(title, message)
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "HIGH_PRIORITY_CHANNEL"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }
}


suspend fun sendNotificationToUser(email: String?, title: String, message: String) {
    email?.let {
        val formattedEmail = it.replace(".", ",")
        val tokenRef = datareference.child("UsersToken").child(formattedEmail).child("/deviceToken")

        try {
            val snapshot = tokenRef.get().await()
            val token = snapshot.getValue(String::class.java)
            Log.e("NotificationKey", "tokenRef: $token")
            if (token != null) {
                sendNotification(token, title, message)
            }
        } catch (e: Exception) {
            println("Error retrieving device token: ${e.message}")
        }
    }
}

suspend fun sendNotification(token: String, title: String, message: String) {
    withContext(Dispatchers.IO) {
        try {
            // Create the JSON payload
            val notification = mapOf(
                "token" to token,
                "title" to title,
                "message" to message
            )
            val jsonNotification = Gson().toJson(notification)

            // Create the request body
            val requestBody = jsonNotification.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            // Create an OkHttp client with logging
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            val client = OkHttpClient.Builder().addInterceptor(logging).build()

            // Build the request
            val request = Request.Builder()
                .url("https://us-central1-myparawale-app.cloudfunctions.net/sendNotification")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build()

            // Execute the request
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    println("Failed to send notification, response code: ${response.code}, error: ${response.body?.string()}")
                } else {
                    println("Notification sent successfully")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Exception occurred: ${e.message}")
        }
    }
}
