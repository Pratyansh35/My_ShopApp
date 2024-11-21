package com.parawale.GrocEase.Notifications

import com.parawale.GrocEase.database.datareference

fun storeDeviceToken(email: String?, token: String) {
        if (email != null) {
            val formattedEmail = email.replace(".", ",")
            val tokenRef =
                datareference.child("UsersToken").child(formattedEmail).child("/deviceToken")
            tokenRef.setValue(token)
        }
 }
fun removeDeviceToken(email: String?) {
        if (email != null) {
            val formattedEmail = email.replace(".", ",")
            val tokenRef =
                datareference.child("UsersToken").child(formattedEmail).child("/deviceToken")
            tokenRef.removeValue()
        }
    }