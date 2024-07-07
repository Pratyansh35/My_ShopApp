package com.example.parawaleapp.mainScreen


import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.parawaleapp.R
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.ui.theme.MyAppTheme

@Composable
fun HomeScreen(
    DishData: List<Dishfordb>,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    cartItems: SnapshotStateList<Dishfordb>,
    total: Double,
    totalmrp: Double,
    updateTotals: () -> Unit,
    saveCartItemsToSharedPreferences: () -> Unit
) {
    MyAppTheme(darkTheme = isDarkTheme) {
        Column {
            LazyColumn {
                item { UpperPanel() }
                item { WeeklySpecial(isDarkTheme = isDarkTheme, onThemeChange = onThemeChange) }
                items(DishData) { Dish ->
                    MenuDish(Dish, cartItems = cartItems, total = total, totalmrp = totalmrp, updateTotals = updateTotals, saveCartItemsToSharedPreferences = saveCartItemsToSharedPreferences)
                }
            }
        }
    }
} 


@Composable
fun UpperPanel() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.secondary)
            .padding(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.title),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary
        )
        Text(
            text = stringResource(id = R.string.location),
            fontSize = 18.sp,
            color = MaterialTheme.colors.onSurface
        )
        Row(
            modifier = Modifier.padding(top = 18.dp),
            verticalAlignment = CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.description),
                color = MaterialTheme.colors.onSurface,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(bottom = 28.dp)
                    .fillMaxWidth(0.6f)
            )
            Image(
                painter = painterResource(id = R.drawable.parawale1),
                contentDescription = "Upper Panel Image",
                modifier = Modifier.clip(RoundedCornerShape(20.dp))
            )
        }
        Button(
            onClick = {
                Toast.makeText(context, "Select Interested Menu", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
        ) {
            Text(
                text = stringResource(id = R.string.orderbuttontext),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onPrimary
            )
        }
    }
}

@Composable
fun WeeklySpecial(isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Weekly Discounts",
                fontSize = 26.sp,
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
            IconButton(onClick = { onThemeChange(!isDarkTheme) }) {
                val icon = if (isDarkTheme) R.drawable.ic_moon else R.drawable.ic_sun
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = if (isDarkTheme) "Dark Mode" else "Light Mode",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

    }
}


@Composable
fun MenuDish(dish: Dishfordb, cartItems: SnapshotStateList<Dishfordb>, total: Double, totalmrp: Double, updateTotals: () -> Unit, saveCartItemsToSharedPreferences: () -> Unit) {
    val context = LocalContext.current
    Card(

        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colors.onSecondary),
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
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                            textDecoration = TextDecoration.LineThrough,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dish.price,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
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
                            updateTotals()
                            saveCartItemsToSharedPreferences()
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
