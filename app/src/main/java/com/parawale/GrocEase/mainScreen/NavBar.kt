package com.parawale.GrocEase.mainScreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.parawale.GrocEase.R
import com.parawale.GrocEase.sign_in.TypewriterText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun NavBar(
    scaffoldState: ScaffoldState? = null,
    scope: CoroutineScope? = null,
    navController: NavController?,
    isDarkTheme : Boolean,
    count: Int
) {
    val cartIcon = if (isDarkTheme) R.drawable.ic_cart_dark else R.drawable.ic_cart_light
    val menuIcon = if (isDarkTheme) R.drawable.ic_menu_light else R.drawable.ic_menu_dark

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = {
            scope?.launch { scaffoldState?.drawerState?.open() }
        }) {
            Image(
                painter = painterResource(menuIcon),
                contentDescription = "menuicon",
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(onClick = {
            navController?.navigate("Location")
        }) {
            Image(
                painter = painterResource(R.drawable.appicon),
                contentDescription = "Icon",
                modifier = Modifier
                    .padding(start = 40.dp)
                    .size(38.dp)
            )
        }
        TypewriterText("Kirana Store!", Modifier.padding(end = 30.dp), 24.sp.value.toInt())

        Box {
            IconButton(onClick = {
                navController?.navigate("cart")
                if (count <= 0) {
                    scope?.launch {
                        scaffoldState?.snackbarHostState?.showSnackbar("Cart is empty")
                    }
                }
            }) {
                Icon(
                    painter = painterResource(cartIcon),
                    contentDescription = "CartIcon",
                    modifier = Modifier
                        .size(35.dp)
                        .padding(start = 10.dp)
                )
            }
            if (count > 0) {
                Text(
                    text = count.toString(),
                    fontSize = 16.sp,
                    color = MaterialTheme.colors.onBackground,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                )
            }
        }
    }
}
