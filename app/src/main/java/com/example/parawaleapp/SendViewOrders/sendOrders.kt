package com.example.parawaleapp.SendViewOrders

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.database.datareference
import com.example.parawaleapp.sign_in.SignInViewModel
import com.example.parawaleapp.sign_in.UserAddressDetails
import com.example.parawaleapp.sign_in.UserData


fun sendOrders(
    context: Context,
    userData: UserData?,
    cartItems: List<Dishfordb>,
    totalMrp: Double,
    totalValue: Double,
    transactionId: String,
    merchantCode: String,
    amountReceived: String,
    amountRemaining: String,
    onPhoneLinkRequired: () -> Unit,
    onSuccessSendNotification: (String) -> Unit,
    onAddressRequired: () -> Unit,
) {
    val merchantEmail = "pratyansh35@gmail.com"
    if (userData == null) {
        Toast.makeText(context, "Please Sign In", Toast.LENGTH_SHORT).show()
        return
    }
    if (cartItems.isEmpty()) {
        Toast.makeText(context, "Cart is Empty", Toast.LENGTH_SHORT).show()
        return
    }
    if(userData.addressDetails == null){
       onAddressRequired()

    }

//    if (userData.userPhoneNumber.isNullOrEmpty()) {
//        // Show linking dialog
//        onPhoneLinkRequired()
//        return
//    }


    val username = userData.userName
    val useremail = userData.userEmail?.replace(
        ".",
        ","
    ) // Replace '.' with ',' to avoid issues in Firebase keys

    val orderDetails = mapOf(
        "totalMrp" to totalMrp,
        "total" to totalValue,
        "items" to cartItems,
        "Order Status" to "Pending",
        "Transaction ID" to transactionId,
        "Merchant Code" to merchantCode,
        "Amount received" to amountReceived,
        "Amount remaining" to amountRemaining
    )

    useremail?.let {
        val userRef = datareference.child("OnlineOrders").child(it)

        userRef.child("username").setValue(username)
        userRef.child("contactno").setValue(userData.userPhoneNumber)
        userRef.child("orders").child(transactionId).setValue(orderDetails).addOnSuccessListener {
                Toast.makeText(context, "Order Placed Successfully", Toast.LENGTH_SHORT).show()
                SignInViewModel().stopLoading()
                onSuccessSendNotification(merchantEmail)
            }.addOnFailureListener { exception ->
                Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                Log.d("booking", "sendOrders: ${exception.message}")
            }
    }
}
