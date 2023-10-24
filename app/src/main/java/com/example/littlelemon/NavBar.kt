package com.example.littlelemon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
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
        Text(
            text = "Kirana Store!",
            fontSize = 24.sp,
            color = Color.Red,
            fontFamily = FontFamily.Cursive,
            modifier = Modifier.padding(end = 30.dp)
        )
        IconButton(onClick = {
            navController?.navigate("cart")
        }) {
            Icon(painter = painterResource(R.drawable.ig_cart),
                contentDescription = "CartIcon",
            modifier = Modifier.size(35.dp).padding(start = 10.dp))
        }

    }
}

