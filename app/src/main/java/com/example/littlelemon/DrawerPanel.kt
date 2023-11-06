package com.example.littlelemon

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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun LeftDrawerPanel(scaffoldState: ScaffoldState, scope: CoroutineScope) {
    Column(
    )
    {
        Card(
            modifier = Modifier.height(150.dp)

        )
        {

            Row(
                modifier = Modifier.background(Brush.horizontalGradient(
                    listOf(Color(0xFFC0454F), Color(0xFF3F4B3D))
                )),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically


            ) {
                Image(
                    painter = painterResource(id = R.drawable.mypic4),
                    contentDescription = "UserImage",
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(130.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(80.dp))
                )


                Column(
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .height(100.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Pratyansh Maddheshia",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFC0B445)

                    )
                    Text(
                        text = "+91-7007254934",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Cursive,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFA9ACA8)

                    )
                }
            }
        }
        LazyColumn(modifier = Modifier.padding(top = 15.dp)) {
            items(SlidesItems) { Slidess ->
                MenuSlide(Slidess)
            }
        }
        IconButton(onClick = {
            scope.launch { scaffoldState.drawerState.close() }
        }) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Close Icon")
        }
    }
}





@Composable
fun CartDrawerPanel( navController: NavController? = null) {
    if (cartItems.isNotEmpty()) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.90f)) {

            LazyColumn {
                items(cartItems) { Dish ->
                    CartItems(Dish, navController)
                }
            }

        }
        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom


        ) {
            Text(
                            text = "Total: ₹${total}",
                            fontFamily = FontFamily.Cursive, fontWeight = FontWeight.W900,
                            color = Color(0xFF555A47),
                            modifier = Modifier.padding(end = 10.dp).align(Alignment.CenterVertically),
                            fontSize = 20.sp
            )


                Button(onClick = {},colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF4CE14)),
                    shape = RoundedCornerShape(40), modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .height(50.dp).align(Alignment.CenterVertically) ) {
                    Text(text = "Proceed to Checkout", color = Color.Black, fontWeight = FontWeight.Bold)

            }
        }}
    }else{
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


fun increment(Dish: Dish): Int {
   return Dish.count + 1

}
fun decrement(Dish: Dish): Int {
    return Dish.count - 1
}

@Preview(showBackground = true)
@Composable
fun PreviewCartDrawerPanel(){
    CartItems(Dish = Dish("Happilo Almonds 500g",
        "₹339",
        0,
        "100% Natural Premium California Dried Almonds are a great source of protein...",
        "DryFruits",
        R.drawable.happilo339))
}

@Composable
fun CartItems(Dish: Dish , navController: NavController? = null) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = Dish.image),
                contentDescription = "",
                Modifier
                    .fillMaxHeight(1f)
            )
            Column(modifier = Modifier.padding(start = 8.dp, top = 3.dp)) {
                Text(
                    text = Dish.name, fontSize = 14.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    text = Dish.description,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(1f)
                        .fillMaxHeight(0.5f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(1f)
                ) {

                    Text(
                        text = Dish.price, color = Color.DarkGray, fontWeight = FontWeight.Bold
                    )

                    val isCartIconVisible = Dish.count == 1

                    IconButton(
                        onClick = {
                             Dish.count = decrement(Dish)
                            if(count > 0 ) {
                                count -= 1
                                total -= Dish.price.removePrefix("₹").toInt()
                                navController?.navigate("cart")
                            }
                            if (Dish.count <= 0) {
                                cartItems.remove(Dish)
                                navController?.navigate("cart")
                            }
                        },
                        Modifier
                            .width(50.dp)
                            .align(Alignment.Top)
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

                    androidx.compose.material3.Text(
                        text =  Dish.count.toString(),
                        fontWeight = FontWeight.W700,
                        textAlign = TextAlign.Center,

                        )
                    IconButton(
                        onClick = { Dish.count = increment(Dish)
                            total += Dish.price.removePrefix("₹").toInt()
                            count += 1
                            navController?.navigate("cart")

                            },
                        Modifier
                            .width(50.dp)
                            .align(Alignment.Top)
                    ) {
                        Text(text = "+")
                    }

                    Column(
                        horizontalAlignment = Alignment.End, modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp)
                    ) {
                        Row {
                            Text(
                                text = "Total:",
                                fontFamily = FontFamily.Cursive, fontWeight = FontWeight.W900,
                                color = Color(0xFFC0B445),
                                modifier = Modifier.padding(end = 10.dp)
                            )

                            Text(
                                text = "₹${Dish.count * Dish.price.removePrefix("₹").toInt()}",
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






