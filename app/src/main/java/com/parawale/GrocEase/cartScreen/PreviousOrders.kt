package com.parawale.GrocEase.cartScreen

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.parawale.GrocEase.SendViewOrders.FetchAllOrdersAndUpdateState
import com.parawale.GrocEase.SendViewOrders.AllOrdersList
import com.parawale.GrocEase.SendViewOrders.Cancelled
import com.parawale.GrocEase.SendViewOrders.Completed
import com.parawale.GrocEase.SendViewOrders.OrderCard
import com.parawale.GrocEase.SendViewOrders.OrderStatusSelectBar
import com.parawale.GrocEase.SendViewOrders.Pending
import com.parawale.GrocEase.DataClasses.UserData

@Composable
fun PreviousOrders(navController: NavController, userData: UserData?) {
    FetchAllOrdersAndUpdateState()
    val logedemail = userData?.userEmail?.replace(".", ",")
    val user = AllOrdersList.find { it.email == logedemail }
    val orders = user?.orders?.filter { order ->
        (order.orderStatus == "Completed" && Completed) ||
                (order.orderStatus == "Pending" && Pending) ||
                (order.orderStatus == "Cancelled" && Cancelled)
    } ?: emptyList()

    val name = user?.username ?: "No orders !!"

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
            text = "My Orders",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF775A65)
        )
        OrderStatusSelectBar()
        Text(
            text = "${logedemail?.replace(",", ".")}",
            modifier = Modifier
                .padding(5.dp, top = 20.dp)
                .fillMaxWidth(),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        if (name == "No orders !!") {
            Text(
                text = name,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = name,
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                fontSize = 20.sp,
                textAlign = TextAlign.Center
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
                    OrderCard(navController, logedemail, user?.username, order)
                }
            }
        }
    }
}
