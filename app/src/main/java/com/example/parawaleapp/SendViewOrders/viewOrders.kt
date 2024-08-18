package com.example.parawaleapp.SendViewOrders

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parawaleapp.PaymentUpi.TransactionResult
import com.example.parawaleapp.cartScreen.CartLayout
import com.example.parawaleapp.cartScreen.ConfirmItems
import com.example.parawaleapp.cartScreen.formatForPrinting
import com.example.parawaleapp.cartScreen.printData
import com.example.parawaleapp.cartScreen.selectedPrinter
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.database.datareference
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class EmailOrder(
    val email: String,
    val username: String = "N/A",
    val contactno: String = "N/A",
    val orders: List<OrdersFromEmails> = emptyList()
)

data class OrdersFromEmails(
    val date: String,
    val atTime: String,
    val transactionId: String,
    val totalprice: Double = 0.0,
    val totalmrp: Double = 0.0,
    val orderedItems: List<Dishfordb> = emptyList(),
    val orderStatus: String,
    val merchantCode : String,
    val amountReceived: String,
    val amountRemaining: String
)

var AllOrdersList by mutableStateOf<List<EmailOrder>>(emptyList())

@Composable
fun FetchAllOrdersAndUpdateState() {
    LaunchedEffect(Unit) {
        fetchAllOrders { fetchedOrders ->
            AllOrdersList = fetchedOrders
        }
    }
}

fun fetchAllOrders(callback: (List<EmailOrder>) -> Unit) {
    val userRef = datareference.child("OnlineOrders")

    userRef.get().addOnSuccessListener { dataSnapshot ->
        val allOrders = mutableListOf<EmailOrder>()

        for (userSnapshot in dataSnapshot.children) {
            val email = userSnapshot.key ?: "N/A"
            val username = userSnapshot.child("username").getValue(String::class.java) ?: "N/A"
            val contactno = userSnapshot.child("contactno").getValue(String::class.java) ?: "N/A"
            val ordersSnapshot = userSnapshot.child("orders")
            val orders = mutableListOf<OrdersFromEmails>()

            for (orderSnapshot in ordersSnapshot.children) {
                try {
                    val orderTimestamp = orderSnapshot.key?.toLongOrNull() ?: 0L
                    val dateFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())
                    val date = dateFormat.format(Date(orderTimestamp))
                    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val atTime = timeFormat.format(Date(orderTimestamp))
                    val totalAmountReceived = orderSnapshot.child("Amount received").getValue(String::class.java) ?: "0"
                    val totalAmountRemaining = orderSnapshot.child("Amount remaining").getValue(String::class.java) ?: "0"
                    val merchantCode = orderSnapshot.child("Merchant Code").getValue(String::class.java) ?: ""
                    val totalMrp = orderSnapshot.child("totalMrp").getValue(Double::class.java) ?: 0.0
                    val total = orderSnapshot.child("total").getValue(Double::class.java) ?: 0.0
                    val orderStatus = orderSnapshot.child("Order Status").getValue(String::class.java) ?: ""

                    val items = mutableListOf<Dishfordb>()
                    val itemsSnapshot = orderSnapshot.child("items")

                    // Check if itemsSnapshot exists and has children (indicating it's a list)
                    if (itemsSnapshot.exists() && itemsSnapshot.hasChildren()) {
                        items.addAll(itemsSnapshot.children.mapNotNull {
                            it.getValue(Dishfordb::class.java)
                        })
                    } else {
                        // Handle case where items is not a list
                        val singleItem = itemsSnapshot.getValue(Dishfordb::class.java)
                        if (singleItem != null) {
                            items.add(singleItem)
                        } else {
                            Log.d("fetchAllOrders", "Items not found for order: $orderTimestamp")
                        }
                    }

                    orders.add(
                        OrdersFromEmails(
                            date = date,
                            atTime = atTime,
                            transactionId = orderTimestamp.toString(),
                            totalprice = total,
                            totalmrp = totalMrp,
                            orderedItems = items,
                            orderStatus = orderStatus,
                            merchantCode = merchantCode,
                            amountReceived = totalAmountReceived,
                            amountRemaining = totalAmountRemaining
                        )
                    )
                } catch (e: Exception) {
                    Log.e("fetchAllOrders", "Error processing order: ${e.message}", e)
                }
            }

            allOrders.add(EmailOrder(email, username, contactno, orders))
        }

        callback(allOrders)
    }.addOnFailureListener { exception ->
        Log.d("fetchAllOrders", "Error fetching all orders: ${exception.message}")
    }
}

@Composable
fun ViewOrders(navController: NavController) {
    FetchAllOrdersAndUpdateState()

    val orderList = when {
        Pending -> AllOrdersList.filter { order -> order.orders.any { it.orderStatus == "Pending" } }
        Completed -> AllOrdersList.filter { order -> order.orders.any { it.orderStatus == "Completed" } }
        Cancelled -> AllOrdersList.filter { order -> order.orders.any { it.orderStatus == "Cancelled" } }
        else -> AllOrdersList
    }

    Column {
        Text(
            text = "Customers Orders",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground
        )
        OrderStatusSelectBar()

        if (orderList.isEmpty()) {
            Text(
                "No orders found",
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onBackground
            )
        } else {
            LazyColumn {
                items(orderList) { order ->
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("personOrders/${order.email}")
                            }, shape = RoundedCornerShape(10.dp), elevation = 10.dp
                    ) {
                        Column {
                            Text(
                                text = "name: ${order.username}",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                                color = MaterialTheme.colors.onSurface
                            )
                            Text(
                                text = "Email: ${order.email.replace(",", ".")}",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                color = MaterialTheme.colors.onSurface
                            )
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Contact: ${order.contactno}",
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "Total Orders: ${order.orders.size}",
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 8.sp,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PersonOrdersScreen(navController: NavController, email: String?) {
    val user = AllOrdersList.find { it.email == email }
    val orders = user?.orders?.filter { order ->
        (order.orderStatus == "Completed" && Completed) ||
                (order.orderStatus == "Pending" && Pending) ||
                (order.orderStatus == "Cancelled" && Cancelled)
    } ?: emptyList()
    val name = user?.username ?: "N/A"

    val totalPendingItems = orders.count { it.orderStatus == "Pending" }
    val totalCompletedItems = orders.count { it.orderStatus == "Completed" }
    val totalCancelledItems = orders.count { it.orderStatus == "Cancelled" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .scrollable(
                rememberScrollState(),
                orientation = Orientation.Vertical
            )
    ) {
        Text(
            text = "Orders from\n ${email?.replace(",", ".")}",
            modifier = Modifier
                .padding(5.dp, top = 20.dp)
                .fillMaxWidth(),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground
        )
        Text(
            text = name,
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground
        )
        if (Pending) {
            Text(
                text = "Total Pending Orders: $totalPendingItems",
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Red
            )
        } else if (Completed) {
            Text(
                text = "Total Completed Orders: $totalCompletedItems",
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Green
            )
        } else if (Cancelled) {
            Text(
                text = "Total Cancelled Orders: $totalCancelledItems",
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
        LazyColumn {
            items(orders) { order ->
                OrderCard(navController, email, user?.username, order)
            }
        }
    }
}

@Composable
fun OrderCard(navController: NavController, email: String?, username: String?, order: OrdersFromEmails) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable {
                val orderedItemsJson = Uri.encode(Gson().toJson(order.orderedItems))
                navController.navigate(
                    "orderDetails/${Uri.encode(email)}/${Uri.encode(order.date)}/$username/$orderedItemsJson/${order.transactionId}"
                )
            }, shape = RoundedCornerShape(10.dp), elevation = 10.dp
    ) {
        Row {
            Column(modifier = Modifier.weight(0.6f)) {
                Text(
                    text = "Date: ${order.date}",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.onSurface
                )
                Text(
                    text = "at: ${order.atTime}",
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    color = MaterialTheme.colors.onSurface
                )
                Text(
                    text = "Total Price: ${order.totalmrp}",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onSurface
                )
            }
            Column(
                modifier = Modifier
                    .weight(.4f)
                    .align(Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Total items", color = MaterialTheme.colors.onSurface)
                Text(
                    text = "${order.orderedItems.size}",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

