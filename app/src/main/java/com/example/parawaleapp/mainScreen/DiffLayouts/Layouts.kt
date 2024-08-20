package com.example.parawaleapp.mainScreen.DiffLayouts

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.mainScreen.truncateString
import com.google.gson.Gson

@Composable
fun GridLayoutItems(
    dish: Dishfordb, cartItems: SnapshotStateList<Dishfordb>, updateTotals: () -> Unit,
    //saveCartItemsToSharedPreferences: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var cartDish = cartItems.find { it.name == dish.name }
    var dishcount by remember {
        mutableIntStateOf(cartDish?.count ?: 0)
    }
    val gson = Gson()

    val discountPercentage = ((dish.mrp.trimStart('₹').toFloat() - dish.price.trimStart('₹')
        .toFloat()) / dish.mrp.trimStart('₹').toFloat()) * 100

    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable {
                val dishJson = gson.toJson(dish)
                navController.navigate("itemDescription/${Uri.encode(dishJson)}")
            }, shape = RoundedCornerShape(16.dp), elevation = 4.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(MaterialTheme.colors.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current).data(dish.imagesUrl[0])
                            .build()
                    ),
                    contentDescription = "Dish Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
                if (discountPercentage > 0) {
                    Text(
                        text = "${discountPercentage.toInt()}% off",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(Color.Red)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = truncateString(dish.name, 30),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dish.weight,
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
                    modifier = Modifier.padding(start = 2.dp),
                    fontSize = 12.sp
                )
                Row(
                    verticalAlignment = CenterVertically, modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(0.4f)
                    ) {
                        Text(
                            text = dish.mrp,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                            textDecoration = TextDecoration.LineThrough,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp
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
                            modifier = Modifier.padding(start = 2.dp),
                            fontSize = 12.sp
                        )
                    }
                    Button(
                        onClick = {
                            if (cartDish != null) {
                                cartDish!!.count++
                            } else {
                                val newDish =
                                    dish.copy(count = 1) // Create a new instance with count 1
                                cartItems.add(newDish)
                            }
                            cartDish = cartItems.find { it.name == dish.name }

                            if (cartDish != null) {
                                dishcount = cartDish!!.count
                            }

                            updateTotals()
                            //saveCartItemsToSharedPreferences()

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(
                                    VibrationEffect.createOneShot(
                                        50, VibrationEffect.DEFAULT_AMPLITUDE
                                    )
                                )
                            } else {
                                vibrator.vibrate(50)
                            }

                            val truncatedString = if (dish.name.length > 8) {
                                dish.name.substring(0, 8) + "..."
                            } else {
                                dish.name
                            }
                            Toast.makeText(
                                context, "Added to Cart: $truncatedString", Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        shape = RoundedCornerShape(40),
                        modifier = Modifier
                            .weight(0.4f)
                            .align(CenterVertically)
                    ) {
                        Text(
                            text = if (dishcount == 0) "Add" else "$dishcount",
                            color = MaterialTheme.colors.onPrimary,
                            fontSize = 6.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LinearLayoutItems(
    dish: Dishfordb, cartItems: SnapshotStateList<Dishfordb>, updateTotals: () -> Unit,
    //saveCartItemsToSharedPreferences: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var cartDish = cartItems.find { it.name == dish.name }
    var dishcount by remember {
        mutableIntStateOf(
            cartDish?.count ?: 0
        )
    }
    val gson = Gson()

    val discountPercentage = ((dish.mrp.trimStart('₹').toFloat() - dish.price.trimStart('₹')
        .toFloat()) / dish.mrp.trimStart('₹').toFloat()) * 100

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.background(MaterialTheme.colors.onSecondary),
            verticalAlignment = CenterVertically
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
                    color = MaterialTheme.colors.onSurface,
                    lineHeight = 12.sp,
                    maxLines = 1
                )
                Text(
                    text = truncateString(dish.description, 65),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 5.dp, bottom = 2.dp),
                    lineHeight = 16.sp,
                    maxLines = 2,
                    minLines = 2
                )
                Row(
                    verticalAlignment = CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(0.4f)
                    ) {
                        Text(
                            text = dish.mrp,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                            textDecoration = TextDecoration.LineThrough,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = dish.price,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = MaterialTheme.colors.onSurface,
                                    offset = Offset(1.0f, 1.0f),
                                    blurRadius = 3f
                                )
                            ),
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(start = 5.dp),
                            fontSize = 14.sp
                        )
                    }
                    Button(
                        onClick = {
                            if (cartDish != null) {
                                cartDish!!.count++
                            } else {
                                val newDish =
                                    dish.copy(count = 1) // Create a new instance with count 1
                                cartItems.add(newDish)
                            }
                            cartDish = cartItems.find { it.name == dish.name }

                            if (cartDish != null) {
                                dishcount = cartDish!!.count
                            }

                            updateTotals()
                            //saveCartItemsToSharedPreferences()

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(
                                    VibrationEffect.createOneShot(
                                        50, VibrationEffect.DEFAULT_AMPLITUDE
                                    )
                                )
                            } else {
                                vibrator.vibrate(50)
                            }

                            val truncatedString = if (dish.name.length > 8) {
                                dish.name.substring(0, 8) + "..."
                            } else {
                                dish.name
                            }
                            Toast.makeText(
                                context, "Added to Cart: $truncatedString", Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        shape = RoundedCornerShape(40),
                        modifier = Modifier
                            .weight(0.5f)
                            .align(CenterVertically)
                    ) {
                        Text(
                            text = if (dishcount == 0) "Add to Cart" else "Add More $dishcount",
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .align(CenterVertically)
                    .padding(end = 4.dp)
            ) {
                AsyncImage(
                    model = Uri.parse(dish.imagesUrl[0]),
                    contentDescription = "dishImage",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            val dishJson = gson.toJson(dish)
                            navController.navigate("itemDescription/${Uri.encode(dishJson)}")
                        },
                )
                if (discountPercentage > 0) {
                    Text(
                        text = "${"%.1f".format(discountPercentage)}% off",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 5.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(Color.Red)
                            .padding(1.dp)
                            .clip(RoundedCornerShape(50.dp))
                    )
                }
            }
        }
    }
    Divider(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
        color = MaterialTheme.colors.onSecondary,
        thickness = 1.dp
    )
}
