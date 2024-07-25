package com.example.parawaleapp.Notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create



class NotificationViewModel: ViewModel() {
    var state by mutableStateOf(notificationState())
        private set

    private val api: FCMapi = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create()

    fun onRemoteTokenChanged(newToken: String) {
        state = state.copy(remoteToken = newToken)
    }

    fun onSubmitRemoteToken() {
        state = state.copy(isEnteringToken = false)
    }

    fun onMessageChange(message: String) {
        state = state.copy(messageText = message)
    }

    fun sendNotification(isBroadcast: Boolean){
        viewModelScope.launch {
            val notificationDto = SendNotificationDTo(
                to = if (isBroadcast) null else state.remoteToken,
                notification = NotificationBody(
                    title = "New Message",
                    body = state.messageText
                )
            )


            try {
                if (isBroadcast) {
                    api.broadcast(notificationDto)
                } else {
                    api.sendNotification(notificationDto)
                }
                state = state.copy(messageText = "")

            } catch (e: HttpException) {
                e.printStackTrace()
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
}