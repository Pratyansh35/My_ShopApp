package com.example.parawaleapp.cartScreen

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parawaleapp.SendViewOrders.FetchAllOrdersAndUpdateState
import com.example.parawaleapp.SendViewOrders.AllOrdersList
import com.example.parawaleapp.sign_in.UserData
import com.google.gson.Gson

@Composable
fun PreviousOrders(navController: NavController, userData: UserData?) {
    FetchAllOrdersAndUpdateState()
    val logedemail = userData?.userEmail?.replace(".", ",")
    val orders = AllOrdersList.find {
        it.email == logedemail
    }?.orders ?: emptyList()

    for (emails in AllOrdersList) {
        Log.e("emaily", " email: ${emails.email} \n  logged email: ${logedemail}")
    }

    val name = AllOrdersList.find { it.email == logedemail }?.username ?: "No orders !!"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .scrollable(
                rememberScrollState(),
                orientation = androidx.compose.foundation.gestures.Orientation.Vertical
            )
    ) {
        Text(
            text = "My orders\n ${logedemail?.replace(",", ".")}",
            modifier = Modifier
                .padding(5.dp, top = 20.dp)
                .fillMaxWidth(),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        if (name == "No orders !!") {
            Text(
                name,
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
            LazyColumn {
                items(orders) { order ->
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clickable {
                                val orderedItemsJson = Uri.encode(Gson().toJson(order.orderedItems))
                                navController.navigate(
                                    "orderDetails/${Uri.encode(logedemail)}/${Uri.encode(order.date)}/${Uri.encode(AllOrdersList.find { it.email == logedemail }?.username)}/$orderedItemsJson"
                                )
                            }, shape = RoundedCornerShape(10.dp), elevation = 10.dp
                    ) {
                        Row {
                            Column(modifier = Modifier.weight(1f)) {
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
                                    text = "at: ${order.atTime}",
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Total Price: ${order.totalmrp}",
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(.3f)
                                    .align(Alignment.CenterVertically)
                            ) {
                                Text(text = "Total items")
                                Text(
                                    text = "${order.orderedItems.size}",
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
    }
}
