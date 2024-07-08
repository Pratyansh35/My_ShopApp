package com.example.parawaleapp.mainScreen

import AddItems
import AppLayout
import BluetoothScreenRoute
import Cart
import PreviousOrders
import ProfileSet
import SettingScreen
import ViewOrder
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.database.Slidess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun MenuListScreen(dataUser: List<Dishfordb>, cartItems: SnapshotStateList<Dishfordb>, updateTotals: () -> Unit, saveCartItemsToSharedPreferences: () -> Unit, navController: NavController) {
    Column {
        Search(dataUser, cartItems, updateTotals, saveCartItemsToSharedPreferences, navController)
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
        backgroundColor = MaterialTheme.colors.onSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 8.dp, 0.dp, 8.dp)
            .height(70.dp)
            ,
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
                    color = MaterialTheme.colors.onSurface,
                    text = slidess.Type,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    color = MaterialTheme.colors.onSurface,
                    text = slidess.Description,
                    fontSize = 14.sp
                )
            }
        }
    }
}

