package com.example.parawaleapp.mainScreen.diffLayouts

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
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
import com.example.parawaleapp.DataClasses.Dishfordb
import com.example.parawaleapp.mainScreen.SlidableDishImage
import com.example.parawaleapp.mainScreen.truncateString
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@Composable
fun GridLayoutItems(
    dish: Dishfordb,
    cartItems: SnapshotStateList<Dishfordb>,
    updateTotals: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    val discountPercentage = calculateDiscount(dish.mrp, dish.price)

    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigateToItemDescription(dish)
            },
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
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
                SlidableDishImage(
                    imageUrls = dish.imagesUrl, // List of dish images
                    darkTheme = isSystemInDarkTheme()
                )
                DiscountBadge(discountPercentage)
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
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PriceSection(dish)
                    CartButton(
                        dish = dish,
                        cartItems = cartItems,
                        updateTotals = updateTotals,
                        vibrator = vibrator,
                        modifier = Modifier
                            .weight(0.4f)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}


@Composable
fun LinearLayoutItems(
    dish: Dishfordb,
    cartItems: SnapshotStateList<Dishfordb>,
    updateTotals: () -> Unit,
    navController: NavController,
    onItemClick: (Dishfordb) -> Unit
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val discountPercentage = calculateDiscount(dish.mrp, dish.price)
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onItemClick(dish) }
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colors.onSecondary)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .padding(end = 8.dp)
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PriceSection(dish)
                    CartButton(
                        dish = dish,
                        cartItems = cartItems,
                        updateTotals = updateTotals,
                        vibrator = vibrator,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .align(Alignment.CenterVertically)
                    .padding(end = 4.dp)
            ) {
                SlidableDishImage(
                    imageUrls = dish.imagesUrl, // List of dish images
                    darkTheme = isSystemInDarkTheme()
                )
                DiscountBadge(discountPercentage)
            }
        }
    }
    Divider(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
        color = MaterialTheme.colors.onSecondary,
        thickness = 1.dp
    )
}



@Composable
fun DishDetailsSheet(dish: Dishfordb) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            dish.imagesUrl.forEach { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(220.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                )
            }
        }

        // Dish name, price, and rating
        Text(text = dish.name, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text(text = dish.description, fontSize = 16.sp, modifier = Modifier.padding(top = 4.dp))
        Row(modifier = Modifier.padding(top = 8.dp)) {
            Text(text = dish.price, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "${dish.rating} ⭐", fontSize = 16.sp)
        }

        // Quantity buttons and Add to Cart button
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            //QuantitySelector() // Implement your own quantity selector
            Button(
                onClick = {
                    // Handle Add to Cart
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(text = "Add Item ${dish.price}", color = Color.White)
            }
        }
    }
}

@Composable
fun CartButton(
    dish: Dishfordb,
    cartItems: SnapshotStateList<Dishfordb>,
    updateTotals: () -> Unit,
    vibrator: Vibrator,
    modifier: Modifier = Modifier // Add this line
) {
    val context = LocalContext.current
    var cartDish = cartItems.find { it.name == dish.name }
    var dishCount by remember { mutableIntStateOf(cartDish?.count ?: 0) }

    Button(
        onClick = {
            if (cartDish != null) {
                cartDish!!.count++
            } else {
                val newDish = dish.copy(count = 1)
                cartItems.add(newDish)
            }
            cartDish = cartItems.find { it.name == dish.name }

            if (cartDish != null) {
                dishCount = cartDish!!.count
            }

            updateTotals()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }

            val truncatedName = dish.name.take(8) + if (dish.name.length > 8) "..." else ""
            Toast.makeText(context, "Added to Cart: $truncatedName", Toast.LENGTH_SHORT).show()
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
        shape = RoundedCornerShape(40),
        modifier = modifier
    ) {
        Text(
            text = if (dishCount == 0) "Add" else "$dishCount",
            color = MaterialTheme.colors.onPrimary,
            fontSize = 12.sp
        )
    }
}



@Composable
fun DiscountBadge(discountPercentage: Float) {
    if (discountPercentage > 0) {
        Text(
            text = "${discountPercentage.toInt()}% off",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 8.sp,
            modifier = Modifier
                .background(Color.Red)
                .padding(2.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}


@Composable
fun DishImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Image(
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build()
        ),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
    )
}

@Composable
fun PriceSection(dish: Dishfordb) {
    Column {
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
}


fun calculateDiscount(mrp: String, price: String): Float {
    val mrpValue = mrp.trimStart('₹').toFloat()
    val priceValue = price.trimStart('₹').toFloat()
    return ((mrpValue - priceValue) / mrpValue) * 100
}

fun NavController.navigateToItemDescription(dish: Dishfordb) {
    val dishJson = Uri.encode(Gson().toJson(dish))
    navigate("itemDescription/$dishJson")
}

