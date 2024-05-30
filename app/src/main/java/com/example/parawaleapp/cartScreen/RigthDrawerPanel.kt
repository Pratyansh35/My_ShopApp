package com.example.parawaleapp.cartScreen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.parawaleapp.R
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.database.cartItems
import com.example.parawaleapp.database.countItems
import com.example.parawaleapp.database.saveCartItemsToSharedPreferences
import com.example.parawaleapp.database.total
import com.example.parawaleapp.database.totalcount
import com.example.parawaleapp.mainScreen.truncateString


@Composable
fun CartDrawerPanel(navController: NavController? = null) {
    if (cartItems.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.90f)
            ) {

                LazyColumn {
                    items(cartItems) { Dish ->
                        CartItems(Dish, navController)
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Total: ₹$total",
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.W900,
                    color = Color(0xFF555A47),
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .align(Alignment.CenterVertically),
                    fontSize = 20.sp
                )


                Button(
                    onClick = {
                        navController?.navigate("AfterCart")
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF4CE14)),
                    shape = RoundedCornerShape(40),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .height(50.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = "Proceed to Checkout",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )

                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ig_cart),
                contentDescription = "Empty Cart",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 20.dp)
            )
            Text(
                text = "Your cart is empty",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC0B445)
            )

        }
    }

}


fun DishIncrement(Dish: Dishfordb, navController: NavController? = null) {
    Dish.count++
    totalcount()
    navController?.navigate("cart")

}

fun DishDecrement(Dish: Dishfordb, navController: NavController? = null) {
    Dish.count--
    totalcount()
    if (Dish.count <= 0) {
        cartItems.remove(Dish)
        countItems()
    }
    navController?.navigate("cart")

}

@Composable
fun CartItems(Dish: Dishfordb, navController: NavController? = null) {
    val context = LocalContext.current
    var Dishcount by remember {
        mutableStateOf(TextFieldValue(Dish.count.toString()))
    }
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp)

        ) {

            AsyncImage(
                model = Uri.parse(Dish.imageUrl),
                contentDescription = "dishImage",
                modifier = Modifier
                    .height(96.dp)
                    .align(Alignment.CenterVertically)

            )
            Column(modifier = Modifier.padding(start = 6.dp, top = 3.dp)) {
                Text(
                    text = truncateString(Dish.name, 30), fontSize = 14.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    text = truncateString(Dish.description, 65),
                    fontSize = 12.sp,
                    color = Color.Gray,

                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(1f)
                        .height(32.dp)
                )
                Row(modifier = Modifier.fillMaxWidth(1f).height((50.dp)), verticalAlignment = Alignment.CenterVertically) {
                    val isCartIconVisible = Dish.count == 1 || Dish.count == 0

                    IconButton(
                        onClick = {
                            DishDecrement(Dish, navController)
                            saveCartItemsToSharedPreferences(context)

                        },
                        Modifier
                            .width(24.dp)
                            .align(Alignment.CenterVertically)
                            .padding(end = 5.dp)
                    ) {
                        if (isCartIconVisible) {
                            Icon(
                                painterResource(id = R.drawable.bin),
                                contentDescription = null,
                            )
                        } else {
                            Text(text = "-")
                        }
                    }
                    TextField(
                        value = Dishcount,
                        onValueChange = {

                            val userInput = it.text.toIntOrNull()
                            if (it.text.isNotEmpty()) {
                                userInput?.let { input ->
                                    if (input > 0) {
                                        Dishcount = it
                                        Dish.count = input
                                        totalcount()
                                        saveCartItemsToSharedPreferences(context)
                                    } else if (input == 0) {
                                        Dishcount = it
                                        Dish.count = input
                                        totalcount()
                                        cartItems.remove(Dish)
                                        countItems()
                                        saveCartItemsToSharedPreferences(context)
                                    } else {

                                    }
                                } ?: run {

                                }
                            } else {
                                Dishcount = it
                                Dish.count = 0
                                totalcount()

                                countItems()
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                        ),
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .align(Alignment.CenterVertically),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
                        singleLine = true,
                        )

                    IconButton(
                        onClick = {
                            DishIncrement(Dish, navController)
                            saveCartItemsToSharedPreferences(context)
                        },
                        Modifier
                            .width(24.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(text = "+")
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(30.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(.5f)) {
                        Text(text = Dish.price, color = Color.Gray, fontWeight = FontWeight.Bold,  style = TextStyle( shadow = Shadow(
                            color = Color.DarkGray, offset = Offset(1.0f, 1.0f), blurRadius = 0f
                        )
                        ),fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, modifier = Modifier.padding(end = 8.dp), fontSize = 16.sp)

                        Text(text = Dish.mrp, color = Color.Gray, textDecoration = TextDecoration.LineThrough, fontSize = 12.sp)
                        Text(
                            text = " -${"%.2f".format(((Dish.mrp.trimStart('₹').toFloat() - Dish.price.trimStart('₹').toFloat()) / Dish.mrp.trimStart('₹').toFloat()) * 100)}%",
                            style = TextStyle(
                                color = Color.Red,
                                shadow = Shadow(
                                    color = Color.DarkGray,
                                    offset = Offset(1.0f, 1.0f),
                                    blurRadius = 3f
                                )
                            ),
                            fontSize = 12.sp
                        )

                    }

                    Column(
                        horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()

                    ) {
                        Row {
                            Text(
                                text = "=",
                                fontFamily = FontFamily.Cursive, fontWeight = FontWeight.W900,
                                color = Color(0xFFC0B445),

                                )

                            Text(
                                text = "₹${Dish.count * Dish.price.removePrefix("₹").toDouble()}",
                                fontFamily = FontFamily.Cursive, fontWeight = FontWeight.Medium,
                                color = Color(0xFF69615A),
                            )
                        }
                    }
                }
            }
        }
    }
    Divider(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
        color = Color.LightGray,
        thickness = 1.dp
    )
}