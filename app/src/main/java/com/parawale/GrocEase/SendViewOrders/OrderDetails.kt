package com.parawale.GrocEase.SendViewOrders

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.parawale.GrocEase.DataClasses.Dishfordb
import com.parawale.GrocEase.Notifications.OrderStatusNotification
import com.parawale.GrocEase.Notifications.sendNotificationToUser
import com.parawale.GrocEase.cartScreen.CartLayout
import com.parawale.GrocEase.cartScreen.ConfirmItems
import com.parawale.GrocEase.cartScreen.formatForPrinting
import com.parawale.GrocEase.cartScreen.printData
import com.parawale.GrocEase.cartScreen.selectedPrinter
import com.parawale.GrocEase.database.datareference
import com.google.gson.Gson
import kotlinx.coroutines.launch

fun updateOrderStatus(
    context: Context,
    userEmail: String?,
    transactionId: String,
    newStatus: String,
    onSuccess: () -> Unit
) {
    userEmail?.let {
        val userEmailFormatted = it.replace(".", ",")
        val orderStatusRef =
            datareference.child("OnlineOrders").child(userEmailFormatted).child("orders")
                .child(transactionId).child("Order Status")

        orderStatusRef.setValue(newStatus).addOnSuccessListener {
                Toast.makeText(context, "Order Status Updated Successfully", Toast.LENGTH_SHORT)
                    .show()
                onSuccess()
            }.addOnFailureListener { exception ->
                Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                Log.d("booking", "updateOrderStatus: ${exception.message}")
            }
    }
}

@Composable
fun OrderDetailsScreen(
    navController: NavController? = null,
    email: String?,
    date: String?,
    timeStamp: String,
    name: String?,
    loggedUser: String?,
    orderedItemsJson: String?
) {
    val context = LocalContext.current
    val order = Gson().fromJson(orderedItemsJson, Array<Dishfordb>::class.java).toList()
    val totalMRP = order.sumOf { it.count * it.mrp.removePrefix("₹").toDouble() }
    val totalPrice = order.sumOf { it.count * it.price.removePrefix("₹").toDouble() }
    var showChangeStatusDialog by remember { mutableStateOf(false) }
    var statusToChange by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LazyColumn(modifier = Modifier.weight(0.8f)) {
            item {
                Row {
                    Button(
                        onClick = {
                            statusToChange = "Cancelled"
                            showChangeStatusDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE01313)),
                        shape = RoundedCornerShape(40),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            text = "Cancel Order",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    Button(
                        onClick = {
                            statusToChange = "Completed"
                            showChangeStatusDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3DF414)),
                        shape = RoundedCornerShape(40),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            text = "Mark as Completed",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    Button(
                        onClick = {
                            statusToChange = "Pending"
                            showChangeStatusDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFDBC70D)),
                        shape = RoundedCornerShape(40),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            text = "Move to Pending",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Text(
                    text = "${email?.replace(",", ".")}",
                    modifier = Modifier
                        .padding(5.dp, top = 20.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    color = Color.Red,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                )
                Text(
                    text = "${name}",
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    color = Color.Red,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                )
                Text(
                    text = "Order Details",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "purchased on ${date}",
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                CartLayout()
            }
            items(order) { dish ->
                ConfirmItems(dish)
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 2.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "MRP: ₹${totalMRP}", modifier = Modifier.padding(5.dp))
            Row(modifier = Modifier.padding(10.dp)) {
                Text(text = "Discount on MRP: ")
                if (order != null) {
                    Text(
                        text = "-₹${totalMRP - totalPrice}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF449C44)
                    )
                }
            }
        }
        Text(
            text = "Total Amount: ₹${totalPrice}",
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        if (loggedUser == "pratyansh35@gmail.com") {
            Button(
                onClick = {
                    if (selectedPrinter.isEmpty()) {
                        Toast.makeText(context, "Please select a printer", Toast.LENGTH_SHORT)
                            .show()
                        navController?.navigate("BluetoothScreenRoute")
                        return@Button
                    }
                    printData(
                        context,
                        selectedPrinter,
                        formatForPrinting(context, order, totalMRP, totalPrice)
                    )
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF4CE14)),
                shape = RoundedCornerShape(40),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.End)
            ) {
                Text(text = "Print Bill", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            if (showChangeStatusDialog) {
                ChangeStatusDialog("Pending",
                    statusToChange,
                    email,
                    date ?: "",
                    timeStamp,
                    timeStamp,
                    onDismiss = { showChangeStatusDialog = false })
            }
        }
    }
}


@Composable
fun ChangeStatusDialog(
    oldStatus: String,
    status: String,
    email: String?,
    date: String,
    time: String,
    transactionId: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showNotification by remember { mutableStateOf(false) }

    if (showNotification) {
        OrderStatusNotification("Order Update", "Your order status is now $status")
        showNotification = false // Reset the state after showing the notification
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Are you sure you want to mark this as $status?") },
        text = {
            Column {
                Text(text = "Transaction ID: $transactionId")
                Text(text = "This will be marked from $oldStatus as $status")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
                updateOrderStatus(context, email, transactionId, status) {
                    showNotification = true
                }
                lifecycleOwner.lifecycleScope.launch {
                    sendNotificationToUser(email, "Order Update", "Your order is now $status")
                }
            }) {
                Text("OK")
            }
        }
    )
}



