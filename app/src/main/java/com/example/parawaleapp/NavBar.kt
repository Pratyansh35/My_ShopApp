package com.example.parawaleapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun NavBar(scaffoldState: ScaffoldState? = null, scope: CoroutineScope? = null , navController: NavController?){
    Row(horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            scope?.launch { scaffoldState?.drawerState?.open() }
        }) {
            Image(painter = painterResource(id = R.drawable.menuicon),
                contentDescription = "menuicon",
                modifier = Modifier.size(24.dp))
        }
        IconButton(onClick = {}) {
            Image(painter = painterResource(R.drawable.appicon), contentDescription = "Icon",
                modifier = Modifier
                    .padding(start = 40.dp)
                    .size(38.dp))
        }
        TypewriterText("Kirana Store!", Modifier.padding(end = 30.dp), 24.sp.value.toInt())


        Box {
            IconButton(onClick = {
                navController?.navigate("cart")
                if (count <=0){
                    scope?.launch {
                        scaffoldState?.snackbarHostState?.showSnackbar("Cart is empty")
                    }
                }
            }) {
                Icon(painter = painterResource(R.drawable.ig_cart),
                    contentDescription = "CartIcon",
                    modifier = Modifier.size(35.dp).padding(start = 10.dp))
            }

            // Display the cart item count
            if (count > 0) {
                Text(
                    text = count.toString(),
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)

                )
            }
        }
    }
}

