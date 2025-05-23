package com.parawale.GrocEase.SendViewOrders

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.parawale.GrocEase.DataClasses.Dishfordb
import com.parawale.GrocEase.DataClasses.UserAddressDetails
import com.parawale.GrocEase.DataClasses.UserData
import com.parawale.GrocEase.database.datareference
import com.parawale.GrocEase.sign_in.SignInViewModel

@Suppress("MissingPermission")
fun sendOrders(
    context: Context,
    userData: UserData?,
    cartItems: List<Dishfordb>,
    totalMrp: Double,
    totalValue: Double,
    transactionId: String,
    merchantCode: String,
    merchantId: String,
    amountReceived: String,
    amountRemaining: String,
    onPhoneLinkRequired: () -> Unit,
    onSuccessSendNotification: () -> Unit,
    onAddressRequired: () -> Unit,
    location: UserAddressDetails?
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
    if (location == null) {
        onAddressRequired()
        return
    }

    val username = userData.userName ?: "N/A"
    val userPhone = userData.userPhoneNumber ?: "N/A"
    val useremail = userData.userEmail?.replace(".", ",") ?: "N/A"

    val currentTime = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC).toString()

    val orderStatusHistory = listOf(
        mapOf(
            "status" to "PENDING",
            "updatedAt" to currentTime,
            "updatedBy" to "Customer"
        )
    )

    val paymentDetails = mapOf(
        "amountReceived" to amountReceived.replace("₹", "").toDoubleOrNull(),
        "amountRemaining" to amountRemaining.replace("₹", "").toDoubleOrNull(),
        "paymentMode" to "Cash",
        "paymentStatus" to "PENDING"
    )

    val customerDetails = mapOf(
        "customerName" to username,
        "customerPhone" to userPhone,
        "customerEmail" to userData.userEmail,
        "customerAddress" to location.address,
        "latitude" to location.latitude.toDoubleOrNull(),
        "longitude" to location.longitude.toDoubleOrNull()
    )

    // Convert Dishfordb to OrderedItem manually
    val orderedItems = cartItems.map {
        mapOf(
            "productId" to (it.barcode ?: ""),
            "variantId" to (it.barcode+3 ?: ""),
            "name" to (it.name ?: ""),
            "brand" to (it.mainCategory ?: ""),
            "count" to it.count,
            "price" to it.price.toString().replace("₹", "").toDoubleOrNull() ,
            "mrp" to it.mrp.toString().replace("₹", "").toDoubleOrNull()
        )
    }

    val orderDetails = mapOf(
        "customerDetails" to customerDetails,
        "totalMRP" to totalMrp,
        "totalPrice" to totalValue,
        "orderedItems" to orderedItems,
        "orderStatus" to "PENDING",
        "orderStatusHistory" to orderStatusHistory,
        "transactionId" to transactionId,
        "merchantCode" to merchantId,
        "paymentDetails" to paymentDetails,
        "date" to currentTime.substringBefore("T"),
        "time" to currentTime
    )

    val merchantOrdersRef = datareference
        .child("merchants")
        .child(merchantCode)
        .child("orders")

    merchantOrdersRef.child(transactionId).setValue(orderDetails)
        .addOnSuccessListener {
            Toast.makeText(context, "Order Placed Successfully", Toast.LENGTH_SHORT).show()
            SignInViewModel().stopLoading()
            onSuccessSendNotification()
        }
        .addOnFailureListener { exception ->
            Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
            Log.d("sendOrders", "Error: ${exception.message}")
        }
}
