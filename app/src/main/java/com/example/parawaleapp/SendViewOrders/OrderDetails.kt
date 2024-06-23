package com.example.parawaleapp.SendViewOrders

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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.google.gson.Gson

@Composable
fun OrderDetailsScreen(
    navController: NavController,
    email: String?,
    date: String?,
    name: String?,
    loggedUser: String?,
    orderedItemsJson: String?
) {
    val context = LocalContext.current
    val order = Gson().fromJson(orderedItemsJson, Array<Dishfordb>::class.java).toList()
    val totalMRP = order.sumOf { it.count * it.mrp.removePrefix("₹").toDouble() }
    val totalPrice = order.sumOf { it.count * it.price.removePrefix("₹").toDouble() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LazyColumn(modifier = Modifier.weight(0.8f)) {
            item {
                Row() {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE01313)),
                        shape = RoundedCornerShape(40),
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth()
                            .height(40.dp)
                            .weight(1f)
                    ) {
                        Text(text = "Cancel Order", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 8.sp, textAlign = TextAlign.Center )
                    }
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3DF414)),
                        shape = RoundedCornerShape(40),
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth()
                            .height(40.dp)
                            .weight(1f)
                    ) {
                        Text(text = "Mark as Completed", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 8.sp, textAlign = TextAlign.Center )
                    }
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFDBC70D)),
                        shape = RoundedCornerShape(40),
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth()
                            .height(40.dp)
                            .weight(1f)
                    ) {
                        Text(text = "Move to Pending", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 8.sp, textAlign = TextAlign.Center )
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
                        navController.navigate("BluetoothScreenRoute")
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

        }
    }
}