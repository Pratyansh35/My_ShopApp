package com.example.parawaleapp.Notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.parawaleapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.http.Body
import retrofit2.http.POST


data class notificationState(
    val isEnteringToken: Boolean = true,
    val remoteToken: String = "",
    val messageText: String = ""
)

data class SendNotificationDTo(
    val to: String?,
    val notification: NotificationBody
)

data class NotificationBody(
    val title: String,
    val body: String
)

interface FCMapi {

    @POST("/send")
    suspend fun sendNotification(@Body body: SendNotificationDTo)

    @POST("/broadcast")
    suspend fun broadcast(@Body body: SendNotificationDTo)
}

class MyFirebaseMessagingService : FirebaseMessagingService()  {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            sendNotification(it.title, it.body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Handle the new token, for example, send it to your server
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
         val datareference = FirebaseDatabase.getInstance().getReference("Tokens")
         datareference.child(FirebaseAuth.getInstance().currentUser?.uid ?: "UnknownUser").setValue(token)
    }

    private fun sendNotification(title: String?, body: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = (System.currentTimeMillis() % 10000).toInt()

        val notificationBuilder = NotificationCompat.Builder(this, "ORDER_STATUS_CHANNEL")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
