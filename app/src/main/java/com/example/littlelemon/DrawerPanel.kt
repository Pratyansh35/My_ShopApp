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
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
fun CartDrawerPanel() {
    Column {

        LazyColumn {
            items(cartItems) { Dish ->
                CartItems(Dish)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,

        ) {
            if (countItems > 0) {
                Column(
                ) {
                    Row {
                        Text(
                            text = "Total:",
                            fontFamily = FontFamily.Cursive, fontWeight = FontWeight.W900,
                            color = Color(0xFFC0B445),
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        Text(
                            text = "₹${countItems * cartItems[0].price.removePrefix("₹").toInt()}",
                            fontFamily = FontFamily.Cursive, fontWeight = FontWeight.Medium,
                            color = Color(0xFF69615A),

                            )
                    }
                }
            }
            if (countItems > 0 ) {
                TextButton(onClick = {

                },Modifier.background(Color(0xFFC0B445)).clip(shape = RoundedCornerShape(50.dp)) ) {
                    Text(text = "Proceed to Checkout", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }


    }

}
@Composable
fun IncrementCount (price: String): Int {
    return countItems + 1
}

/*@Preview(showBackground = true)
@Composable
fun Prev() {
    Column {
        CartItems(
            Dish(
                "Fortune Refined Oil(1ltr)",
                "₹110",
                "fulfills the body's needs for Omega 3 fatty acids Soy Oil...",
                R.drawable.fortune110
            )
        )
    }
}*/


var countItems by  mutableStateOf(0)
val cartItems: MutableList<Dish> = mutableListOf()

@Composable
fun CartItems(Dish: Dish) {

    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Image(
                painter = painterResource(id = Dish.image),
                contentDescription = "",
                Modifier
                    .fillMaxHeight(1f)
            )
            Column(modifier = Modifier.padding(start = 8.dp, top = 3.dp)) {
                Text(
                    text = Dish.name, fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    text = Dish.description,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(1f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(400.dp)
                ) {

                    Text(
                        text = Dish.price, color = Color.DarkGray, fontWeight = FontWeight.Bold
                    )



                    val isCartIconVisible = countItems == 1

                    IconButton(
                        onClick = {
                            countItems -= 1
                            if (countItems == 1) {
                                // Do something when countItems is 1
                            }
                            if (countItems <= 0) {
                                // Remove item from the cart
                                cartItems.remove(Dish)
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
                        text = "$countItems",
                        fontWeight = FontWeight.W700,
                        textAlign = TextAlign.Center,

                        )
                    TextButton(
                        onClick = { countItems += 1 },
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
                                text = "₹${countItems * Dish.price.removePrefix("₹").toInt()}",
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






