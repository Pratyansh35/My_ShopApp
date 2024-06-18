package com.example.parawaleapp.SendViewOrders

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.database.datareference
import com.example.parawaleapp.database.phoneno
import com.example.parawaleapp.sign_in.UserData


fun sendOrders(
    context: Context,
    userData: UserData?,
    cartItems: List<Dishfordb>,
    totalMrp: Double,
    total: Double
) {
    if (userData == null) {
        Toast.makeText(context, "Please Sign In", Toast.LENGTH_SHORT).show()
        return
    }
    if (cartItems.isEmpty()) {
        Toast.makeText(context, "Cart is Empty", Toast.LENGTH_SHORT).show()
        return
    }

    val username = userData.userName
    val useremail = userData.userEmail?.replace(".", ",") // Replace '.' with ',' to avoid issues in Firebase keys
    val orderTimestamp = System.currentTimeMillis().toString()

    val orderDetails = mapOf(
        "totalMrp" to totalMrp,
        "total" to total,
        "items" to cartItems
    )

    useremail?.let {
        val userRef = datareference.child("OnlineOrders").child(it)

        // Save the username at the user level
        userRef.child("username").setValue(username)
        userRef.child("contactno").setValue(phoneno)
        // Save the order under the orders node with the timestamp
        userRef.child("orders").child(orderTimestamp).setValue(orderDetails)
            .addOnSuccessListener {
                Toast.makeText(context, "Order Placed Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                Log.d("booking", "sendOrders: ${exception.message}")
            }
    }
}
