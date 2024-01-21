package com.example.parawaleapp.mainScreen

import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.parawaleapp.database.Slidess
import com.example.parawaleapp.database.cartItems
import com.example.parawaleapp.database.countItems
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
fun MenuSlide(Slidess: Slidess, navController: NavController? = null, scaffoldState: ScaffoldState, scope: CoroutineScope) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 8.dp, 8.dp, 20.dp),



        onClick = { if (Slidess.Type == "Cart"){
            scope.launch { scaffoldState.drawerState.close() }
            navController?.navigate("cart")
        }else if (Slidess.Type == "Manage Account"){
            scope.launch { scaffoldState.drawerState.close() }
            navController?.navigate("ProfileSet")
        }else if (Slidess.Type == "Add Items") {
            scope.launch { scaffoldState.drawerState.close() }
            navController?.navigate("AddItems")
        }

        }

    ) {

        Row {
            Image(
                painter = painterResource(id = Slidess.image),
                contentDescription = "",
                modifier = Modifier
                    .size(50.dp)
                    .padding(8.dp)
            )

            Column {

                Text(
                    text = Slidess.Type,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = Slidess.Description,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}




@Composable
fun MenuDish(dish: Dishfordb) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column {
                Text(
                    text = dish.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text =dish.description,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                        .fillMaxWidth(.75f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(modifier = Modifier.fillMaxWidth(.3f)) {
                        Text(
                            text = dish.price,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(.6f), Arrangement.End) {
                        Button(
                            onClick = { if (cartItems.contains(dish)) {
                                dish.count++
                                countItems()
                                totalcount()
                            } else {
                                dish.count++
                                cartItems.add(dish)
                                totalcount()
                                countItems()

                            }},
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
