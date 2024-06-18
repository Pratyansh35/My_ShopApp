package com.example.parawaleapp.SendViewOrders

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
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
import com.example.parawaleapp.cartScreen.CartLayout
import com.example.parawaleapp.cartScreen.ConfirmItems
import com.example.parawaleapp.cartScreen.formatForPrinting
import com.example.parawaleapp.cartScreen.printData
import com.example.parawaleapp.cartScreen.selectedPrinter
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.database.cartItems
import com.example.parawaleapp.database.datareference
import com.example.parawaleapp.database.total
import com.example.parawaleapp.database.totalmrp
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
    val totalprice: Double = 0.0,
    val totalmrp: Double = 0.0,
    val orderedItems: List<Dishfordb> = emptyList()
)

var orderList by mutableStateOf<List<EmailOrder>>(emptyList())


@Composable
fun FetchAllOrdersAndUpdateState() {
    LaunchedEffect(Unit) {
        fetchAllOrders { fetchedOrders ->
            orderList = fetchedOrders
        }
    }
}

fun fetchAllOrders(callback: (List<EmailOrder>) -> Unit) {
    val userRef = datareference.child("OnlineOrders")

    userRef.get().addOnSuccessListener { dataSnapshot ->
        val allOrders = mutableListOf<EmailOrder>()

        for (userSnapshot in dataSnapshot.children) {
            val email = userSnapshot.key ?: ""
            val username = userSnapshot.child("username").getValue(String::class.java) ?: ""
            val contactno = userSnapshot.child("contactno").getValue(String::class.java) ?: ""
            val ordersSnapshot = userSnapshot.child("orders")
            val orders = mutableListOf<OrdersFromEmails>()

            for (orderSnapshot in ordersSnapshot.children) {
                val simpleDateFormat = SimpleDateFormat("dd/mm/yy", Locale.getDefault())
                val date = simpleDateFormat.format(Date(orderSnapshot.key?.toLong() ?: 0))

                Log.e("gotdate", date)
                val totalMrp = orderSnapshot.child("totalMrp").getValue(Double::class.java) ?: 0.0
                val total = orderSnapshot.child("total").getValue(Double::class.java) ?: 0.0
                val items = orderSnapshot.child("items").children.map {
                    it.getValue(Dishfordb::class.java)!!
                }
                orders.add(OrdersFromEmails(date, totalMrp, total, items))
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

    if (orderList.isEmpty()) {
        Text(
            "No orders found",
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            textAlign = TextAlign.Center
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
                            text = "Username: ${order.username}",
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "Email: ${order.email.replace(",", ".")}",
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Contact: ${order.contactno}",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Total Orders: ${order.orders.size}",
                                modifier = Modifier
                                    .padding(2.dp)
                                    .weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 8.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PersonOrdersScreen(navController: NavController, email: String?) {
    val orders = orderList.find { it.email == email }?.orders ?: emptyList()
    val name = orderList.find { it.email == email }?.username ?: "N/A"
    Column {
        Text(
            text = "Orders for\n ${email?.replace(",", ".")}",
            modifier = Modifier.padding(5.dp, top = 20.dp).fillMaxWidth(),
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = name,
            modifier = Modifier.padding(5.dp).fillMaxWidth(),
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        LazyColumn {
            items(orders) { order ->
                Card(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(
                                "orderDetails/${Uri.encode(email)}/${
                                    Uri.encode(
                                        order.date
                                    )
                                }/${orderList.find { it.email == email }?.username}"
                            )
                        }, shape = RoundedCornerShape(10.dp), elevation = 10.dp
                ) {
                    Column {
                        Text(
                            text = "Date: ${order.date}",
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "Total Price: ${order.totalprice}",
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun OrderDetailsScreen(navController: NavController, email: String?, date: String?, name:String?) {
    val orders = orderList.find { it.email == email }?.orders ?: emptyList()
    val order = orders.find { it.date == date }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(modifier = Modifier.weight(0.8f)) {
            item {
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
                CartLayout()
            }
            items(order?.orderedItems ?: emptyList()) { dish ->
                ConfirmItems(dish)
            }
        }
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "MRP: ₹${order?.totalprice}", modifier = Modifier.padding(10.dp))
            Row(modifier = Modifier.padding(10.dp)) {
                Text(text = "Discount on MRP: ")
                if (order != null) {
                    Text(
                        text = "-₹${order.totalprice - order.totalmrp }",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF449C44)
                    )
                }
            }
        }
        Text(
            text = "Total Amount: ₹${order?.totalmrp}",
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Button(
            onClick = {
                if (selectedPrinter.isEmpty()) {
                    Toast.makeText(context, "Please select a printer", Toast.LENGTH_SHORT).show()
                    navController.navigate("BluetoothScreenRoute")
                    return@Button
                }

                val printData =
                    order?.let { formatForPrinting(context, it.orderedItems , order.totalmrp, order.totalprice) }
                if (printData != null) {
                    printData(context, selectedPrinter, printData)
                }
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

    }

}
