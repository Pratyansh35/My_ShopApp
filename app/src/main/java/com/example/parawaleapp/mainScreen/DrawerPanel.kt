package com.example.parawaleapp.mainScreen

import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.ScaffoldState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.parawaleapp.R
import com.example.parawaleapp.database.Dish
import com.example.parawaleapp.database.SlidesItems
import com.example.parawaleapp.database.cartItems
import com.example.parawaleapp.database.clearDataFromSharedPreferences
import com.example.parawaleapp.database.countItems
import com.example.parawaleapp.database.img
import com.example.parawaleapp.database.name
import com.example.parawaleapp.database.total
import com.example.parawaleapp.database.totalcount
import com.example.parawaleapp.sign_in.UserData
import kotlinx.coroutines.CoroutineScope


@Composable
fun LeftDrawerPanel(
    scaffoldState: ScaffoldState, navController: NavController? = null, scope: CoroutineScope,
    userData: UserData?,
    signOut: () -> Unit
) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth()
    )
    {
        Card(
            modifier = Modifier.height(150.dp).fillMaxWidth()
        )
        {
            Row(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0E0A0B), Color(0xFF707A6D))
                        )
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically


            ) {



                    AsyncImage(model = userData?.progilePictureUrl
                    ,
                        contentDescription = "userImage",
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(110.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .height(100.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    if (userData?.userName != null) {
                        Text(
                            text =
                            if(name == ""){
                                userData.userName
                                }else(
                                name
                                ),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFC0B445)
                        )
                    }
                    if (userData?.userEmail != null) {
                        Text(
                            text = userData.userEmail,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            textAlign = TextAlign.Center,
                            color = Color(0xFFC9B9B9)

                        )
                    }
                    
                }
            }
        }
        LazyColumn(modifier = Modifier.padding(top = 15.dp)) {
            items(SlidesItems) { Slidess ->
                MenuSlide(Slidess, scope = scope, scaffoldState = scaffoldState, navController = navController)
            }
        }
        

        Row(Modifier.fillMaxWidth().fillMaxHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom


        ) {
            Button(onClick = {
                signOut()
                clearDataFromSharedPreferences(context);
            },colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF4CE14)),
                shape = RoundedCornerShape(40), modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.CenterVertically) ) {
                Text(text = "LOG_OUT", color = Color.Black, fontWeight = FontWeight.Bold)
            }
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
                            text = "Total: ₹$total",
                            fontFamily = FontFamily.Cursive, fontWeight = FontWeight.W900,
                            color = Color(0xFF555A47),
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .align(Alignment.CenterVertically),
                            fontSize = 20.sp
            )


                Button(onClick = {
                    navController?.navigate("AfterCart")
                },colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF4CE14)),
                    shape = RoundedCornerShape(40), modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .height(50.dp)
                        .align(Alignment.CenterVertically) ) {
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






fun DishIncrement(Dish: Dish, navController: NavController? = null){
    Dish.count++
    totalcount()
    navController?.navigate("cart")
}
fun DishDecrement(Dish: Dish, navController: NavController? = null){
    Dish.count--
    totalcount()
    if (Dish.count <= 0) {
        cartItems.remove(Dish)
        countItems()
    }
    navController?.navigate("cart")

}

@Composable
fun CartItems(Dish: Dish, navController: NavController? = null) {
    var Dishcount by remember {
        mutableStateOf(TextFieldValue(Dish.count.toString()))
    }

    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = Dish.image),
                contentDescription = "",
                Modifier
                    .fillMaxHeight(0.8f)
                    .align(Alignment.CenterVertically)
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
                        .fillMaxHeight(0.45f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(1f)
                ) {

                    Text(
                        text = Dish.price, color = Color.DarkGray, fontWeight = FontWeight.Bold
                    )

                    val isCartIconVisible = Dish.count == 1 || Dish.count == 0

                    IconButton(
                        onClick = {
                                  DishDecrement(Dish,navController)
                        },
                        Modifier
                            .width(36.dp)
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
                                    } else if(input == 0){
                                        Dishcount = it
                                        Dish.count = input
                                        totalcount()
                                        cartItems.remove(Dish)
                                        countItems()
                                    }
                                    else {

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
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done),
                        modifier = Modifier
                            .width(50.dp)
                            .fillMaxHeight()
                            .align(Alignment.CenterVertically),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),



                    )

                    IconButton(
                        onClick = {
                            DishIncrement(Dish,  navController)
                                  },
                        Modifier
                            .width(36.dp)
                            .align(Alignment.Top)
                    ) {
                        Text(text = "+")
                    }

                    Column(
                        horizontalAlignment = Alignment.End, modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Row {
                            Text(
                                text = "Total:",
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






