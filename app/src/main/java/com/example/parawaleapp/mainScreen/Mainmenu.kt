package com.example.parawaleapp.mainScreen

import PreviousOrders
import SettingScreen
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.parawaleapp.R
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

fun truncateString(text: String, maxLength: Int): String {
    return if (text.length > maxLength) {
        text.substring(0, maxLength) + "..."
    } else {
        text
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
            .padding(0.dp, 8.dp, 0.dp, 8.dp)
            .height(70.dp),
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
                "App Layout" -> AppLayout.route
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
                    .padding(start = 15.dp)
                    .align(Alignment.CenterVertically)
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
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
    Card(
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colors.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .padding(8.dp)
            ) {
                Text(
                    text = truncateString(dish.name, 25),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface
                )
                Text(
                    text = truncateString(dish.description, 65),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(0.4f)
                    ) {
                        Text(
                            text = dish.mrp,
                            color = MaterialTheme.colors.onSecondary.copy(alpha = 0.6f),
                            textDecoration = TextDecoration.LineThrough,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dish.price,
                            color = MaterialTheme.colors.onSecondary.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = MaterialTheme.colors.onSecondary,
                                    offset = Offset(1.0f, 1.0f),
                                    blurRadius = 3f
                                )
                            ),
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                    Button(
                        onClick = {
                            if (!cartItems.contains(dish)) {
                                cartItems.add(dish)
                            }
                            dish.count++
                            countItems()
                            totalcount()
                            saveCartItemsToSharedPreferences(context)
                            val truncatedString = if (dish.name.length > 8) {
                                dish.name.substring(0, 8) + "..."
                            } else {
                                dish.name
                            }
                            Toast.makeText(context, "Added to Cart: $truncatedString", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        shape = RoundedCornerShape(40),
                        modifier = Modifier
                            .weight(0.5f)
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp)
                    ) {
                        Text(text = "Add to Cart", color = MaterialTheme.colors.onPrimary)
                    }
                }
            }
            AsyncImage(
                model = Uri.parse(dish.imageUrl),
                contentDescription = "dishImage",
                modifier = Modifier
                    .size(108.dp) // Set a fixed size for the image
                    .clip(RoundedCornerShape(20.dp))
                    .align(Alignment.CenterVertically)
                    .padding(end = 8.dp)
            )
        }
    }
    Divider(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
        color = MaterialTheme.colors.onSecondary,
        thickness = 1.dp
    )
}
