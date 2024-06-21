package com.example.parawaleapp.mainScreen

import PreviousOrders
import SettingScreen
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.database.Slidess
import com.example.parawaleapp.database.cartItems
import com.example.parawaleapp.database.countItems
import com.example.parawaleapp.database.saveCartItemsToSharedPreferences
import com.example.parawaleapp.database.totalcount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun MenuListScreen(dataUser: List<Dishfordb>) {
    Column {
        Search(dataUser)
    }
}


@Composable
fun MenuCategory(category: String) {
    Button(
        onClick = { /*TODO*/ },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
        shape = RoundedCornerShape(40),
        modifier = Modifier.padding(5.dp)
    ) {
        Text(
            text = category
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MenuSlide(
    slidess: Slidess,
    navController: NavController? = null,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 8.dp, 8.dp, 20.dp),
        onClick = {
            scope.launch { scaffoldState.drawerState.close() }
            val route = when (slidess.Type) {
                "Cart" -> Cart.route
                "Manage Account" -> ProfileSet.route
                "Add Items" -> AddItems.route
                "Connect Printer" -> BluetoothScreenRoute.route
                "Customers Order" -> ViewOrder.route
                "Orders" -> PreviousOrders.route
                "Settings" -> SettingScreen.route
                else -> null
            }
            route?.let { navController?.navigate(it) }
        }
    ) {
        Row {
            Image(
                painter = painterResource(id = slidess.image),
                contentDescription = slidess.Type,
                modifier = Modifier
                    .size(50.dp)
                    .padding(8.dp)
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = slidess.Type,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = slidess.Description,
                    fontSize = 14.sp
                )
            }
        }
    }
}




@Composable
fun MenuDish(dish: Dishfordb) {
    val context = LocalContext.current
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column {
                Text(
                    text = truncateString(dish.name, 25),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = truncateString(dish.description, 65),
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                        .fillMaxWidth(.75f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(modifier = Modifier.fillMaxWidth(.3f)) {
                        Text(text = dish.mrp, color = Color.Gray, textDecoration = TextDecoration.LineThrough)

                        Text(text = dish.price, color = Color.Gray, fontWeight = FontWeight.Bold,  style = TextStyle( shadow = Shadow(
                            color = Color.DarkGray, offset = Offset(1.0f, 1.0f), blurRadius = 3f
                        )
                        ),fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, modifier = Modifier.padding(start = 5.dp))
                    }
                    Row(modifier = Modifier.fillMaxWidth(.6f), Arrangement.End) {
                        Button(
                            onClick = {
                                if (!cartItems.contains(dish)) {
                                    cartItems.add(dish)
                                }
                                dish.count++
                                countItems()
                                totalcount()
                                saveCartItemsToSharedPreferences(context)
                                val charLen = dish.name.length;
                                val truncatedString = if (charLen > 8) { dish.name.substring(0, 8) + "..." } else { dish.name }
                                Toast.makeText(context, "Added to Cart: ${truncatedString}", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF4CE14)),
                            shape = RoundedCornerShape(40)
                        ) {
                            Text(text = "Add to Cart")
                        }
                    }
                }
            }
            AsyncImage(
                model = Uri.parse(dish.imageUrl),
                contentDescription = "dishImage",
            )
        }
    }
    Divider(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
        color = Color.LightGray,
        thickness = 1.dp
    )
}


fun truncateString(text: String, maxLength: Int): String {
    return if (text.length > maxLength) {
        text.substring(0, maxLength) + "..."
    } else {
        text
    }
}