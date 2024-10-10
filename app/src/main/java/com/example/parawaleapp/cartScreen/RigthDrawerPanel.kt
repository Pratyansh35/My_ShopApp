package com.example.parawaleapp.cartScreen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.parawaleapp.DataClasses.Dishfordb
import com.example.parawaleapp.R
import com.example.parawaleapp.mainScreen.truncateString

@Composable
fun CartDrawerPanel(
    navController: NavController? = null,
    cartItems: MutableList<Dishfordb>,
    allOverTotalPrice: Double,
    updateTotals: () -> Unit,
    //saveCartItemsToSharedPreferences: () -> Unit
) {
    if (cartItems.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.90f)
            ) {
                LazyColumn {
                    items(cartItems) { dish ->
                        CartItems(dish,
                            cartItems,
                            updateTotals,
                            //saveCartItemsToSharedPreferences
                        )
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Total: ₹${allOverTotalPrice}",
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.W900,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .align(Alignment.CenterVertically),
                    fontSize = 16.sp
                )

                Button(
                    onClick = {
                        navController?.navigate("AfterCart")
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                    shape = RoundedCornerShape(40),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = "Proceed to Checkout",
                        color = MaterialTheme.colors.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ig_cart),
                contentDescription = "Empty Cart",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 20.dp)
            )
            Text(
                text = "Your cart is empty",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.secondary
            )
        }
    }
}


@Composable
fun CartItems(
    dish: Dishfordb,
    cartItems: MutableList<Dishfordb>,
    updateTotals: () -> Unit,
    //saveCartItemsToSharedPreferences: () -> Unit
) {
    var dishcount by remember { mutableStateOf(TextFieldValue(dish.count.toString())) }
    val context = LocalContext.current
    var removeConfirm by remember { mutableStateOf(false) }
    val discountPercentage = ((dish.mrp.trimStart('₹').toFloat() - dish.price.trimStart('₹').toFloat()) / dish.mrp.trimStart('₹').toFloat()) * 100
    Card(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .align(Alignment.CenterVertically)
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
                Text(
                    text = "${discountPercentage.toInt()}% off",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 6.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color.Red)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxHeight()
                    .padding(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.7f)
                            .padding(0.dp)
                            .height(54.dp)
                            .align(Alignment.Top)
                    ) {
                        Text(
                            text = dish.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.onSurface,
                            minLines = 2,
                            maxLines = 2,
                            lineHeight = 14.sp,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(0.dp)
                        )
                        Text(
                            text = truncateString(dish.weight, 55),
                            fontSize = 12.sp,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(0.dp)
                        )
                    }
                    IconButton(
                        onClick = { removeConfirm = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.3f)
                            .size(36.dp)
                            .padding(0.dp)
                            .align(Alignment.CenterVertically),

                        ) {
                        Icon(
                            painterResource(id = R.drawable.removeitem),
                            contentDescription = "Remove Item",
                            tint = MaterialTheme.colors.onSurface,
                            modifier = Modifier.fillMaxSize(.5f)

                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {
                            if (dish.count > 0) {
                                dish.count--
                                dishcount = TextFieldValue(dish.count.toString())
                                if (dish.count == 0) {
                                    cartItems.remove(dish)
                                }
                                updateTotals()
                                //saveCartItemsToSharedPreferences()
                            }
                        }, modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = if (dish.count == 0) painterResource(id = R.drawable.bin) else painterResource(
                                id = R.drawable.ic_minus
                            ), contentDescription = null, tint = MaterialTheme.colors.onSurface
                        )
                    }
                    TextField(
                        value = dishcount,
                        onValueChange = {
                            val userInput = it.text.toIntOrNull()
                            if (it.text.isNotEmpty() && userInput != null && userInput >= 0) {
                                dishcount = it
                                dish.count = userInput
                                updateTotals()
                                //saveCartItemsToSharedPreferences()
                                if (userInput == 0) {
                                    cartItems.remove(dish)
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                        ),
                        modifier = Modifier
                            .width(64.dp)
                            .padding(horizontal = 4.dp),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            dish.count++
                            dishcount = TextFieldValue(dish.count.toString())
                            updateTotals()
                            //saveCartItemsToSharedPreferences()
                        }, modifier = Modifier.size(12.dp)
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_add),
                            contentDescription = null,
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Text(
                        text = "${dish.price}",
                        color = MaterialTheme.colors.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontSize = 16.sp,

                        )
                    Text(
                        text = dish.mrp,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                        textDecoration = TextDecoration.LineThrough,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Text(
                        text = "=₹${dish.count * dish.price.removePrefix("₹").toDouble()}",
                        fontFamily = FontFamily.Cursive,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 8.dp).fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
        if (removeConfirm) {
            AlertDialog(onDismissRequest = { removeConfirm = false },
                title = { Text("Confirm Removal") },
                text = { Text("Are you sure you want to remove ${dish.name}?") },
                confirmButton = {
                    Button(onClick = {
                        dish.count = 0
                        cartItems.remove(dish)
                        updateTotals()
                        //saveCartItemsToSharedPreferences()
                        Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show()
                        removeConfirm = false
                    }) {
                        Text("Remove")
                    }
                },
                dismissButton = {
                    Button(onClick = { removeConfirm = false }) {
                        Text("Cancel")
                    }
                })
        }
    }
    Divider(
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}


//@Composable
//fun CartItems(
//    dish: Dishfordb,
//    cartItems: MutableList<Dishfordb>,
//    updateTotals: () -> Unit,
//    saveCartItemsToSharedPreferences: () -> Unit
//) {
//    var dishcount by remember { mutableStateOf(TextFieldValue(dish.count.toString())) }
//    var context = LocalContext.current
//    var removeConfirm by remember { mutableStateOf(false) }
//    Card {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(148.dp)
//        ) {
//            AsyncImage(
//                model = Uri.parse(dish.imageUrl),
//                contentDescription = "dishImage",
//                modifier = Modifier
//                    .height(96.dp)
//                    .weight(0.3f)
//                    .align(Alignment.CenterVertically)
//            )
//            Column(modifier = Modifier
//                .padding(start = 6.dp, top = 3.dp)
//                .weight(0.7f)
//                .align(Alignment.CenterVertically)
//            ) {
//                Row(modifier = Modifier.fillMaxWidth()) {
//                    Column(modifier = Modifier.fillMaxWidth(0.8f)) {
//                        Text(
//                            text = truncateString(dish.name, 25),
//                            fontSize = 14.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = MaterialTheme.colors.onSurface,
//                            lineHeight = 12.sp,
//                            minLines = 2,
//                            maxLines = 2
//                        )
//                        Text(
//                            text = truncateString(dish.description, 55),
//                            fontSize = 12.sp,
//                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            lineHeight = 12.sp,
//                            minLines = 2,
//                            maxLines = 2
//
//                        )
//                    }
//                    IconButton(
//                        onClick = { removeConfirm = true }, // Show the dialog on click
//                        Modifier
//                            .size(64.dp)
//                            .height(40.dp)
//                            .align(Alignment.CenterVertically)
//                    ) {
//                        Icon(
//                            painterResource(id = R.drawable.removeitem),
//                            contentDescription = "Remove Item", // Add content description
//                            tint = MaterialTheme.colors.onSurface,
//                            modifier = Modifier.size(40.dp)
//                        )
//                    }
//                }
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    val isCartIconVisible = dish.count == 1 || dish.count == 0
//
//                    IconButton(
//                        onClick = {
//                            dish.count--
//                            dishcount = TextFieldValue(dish.count.toString())
//                            if (dish.count <= 0) {
//                                cartItems.remove(dish)
//                            }
//                            updateTotals()
//                            saveCartItemsToSharedPreferences()
//                        },
//                        Modifier
//                            .width(24.dp)
//                            .align(Alignment.CenterVertically)
//                            .padding(end = 5.dp)
//                    ) {
//                        if (isCartIconVisible) {
//                            Icon(
//                                painterResource(id = R.drawable.bin),
//                                contentDescription = null,
//                                tint = MaterialTheme.colors.onSurface
//                            )
//                        } else {
//                            Text(text = "-", color = MaterialTheme.colors.onSurface)
//                        }
//                    }
//                    TextField(
//                        value = dishcount,
//                        onValueChange = {
//                            val userInput = it.text.toIntOrNull()
//                            if (it.text.isNotEmpty()) {
//                                userInput?.let { input ->
//                                    if (input > 0) {
//                                        dishcount = it
//                                        dish.count = input
//                                        updateTotals()
//                                        saveCartItemsToSharedPreferences()
//                                    } else if (input == 0) {
//                                        dishcount = it
//                                        dish.count = input
//                                        updateTotals()
//                                        cartItems.remove(dish)
//                                        saveCartItemsToSharedPreferences()
//                                    }
//                                }
//                            } else {
//                                dishcount = it
//                                dish.count = 0
//                                updateTotals()
//                            }
//                        },
//                        keyboardOptions = KeyboardOptions.Default.copy(
//                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
//                        ),
//                        modifier = Modifier
//                            .width(50.dp)
//
//                            .align(Alignment.CenterVertically),
//                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
//                        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
//                        singleLine = true,
//                    )
//                    IconButton(
//                        onClick = {
//                            dish.count++
//                            dishcount = TextFieldValue(dish.count.toString())
//                            updateTotals()
//                            saveCartItemsToSharedPreferences()
//                        },
//                        Modifier
//                            .width(24.dp)
//                            .align(Alignment.CenterVertically)
//                    ) {
//                        Text(text = "+", color = MaterialTheme.colors.onSurface)
//                    }
//                }
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(30.dp)
//                ) {
//                    Row(modifier = Modifier.fillMaxWidth(.5f)) {
//                        Text(
//                            text = dish.price,
//                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
//                            fontWeight = FontWeight.Bold,
//                            style = TextStyle(
//                                shadow = Shadow(
//                                    color = MaterialTheme.colors.onSurface,
//                                    offset = Offset(1.0f, 1.0f),
//                                    blurRadius = 0f
//                                )
//                            ),
//                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
//                            modifier = Modifier.padding(end = 8.dp),
//                            fontSize = 16.sp
//                        )
//                        Text(
//                            text = dish.mrp,
//                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
//                            textDecoration = TextDecoration.LineThrough,
//                            fontSize = 12.sp
//                        )
//                        Text(
//                            text = " -${
//                                "%.2f".format(
//                                    ((dish.mrp.trimStart('₹').toFloat() - dish.price.trimStart('₹')
//                                        .toFloat()) / dish.mrp.trimStart('₹').toFloat()) * 100
//                                )
//                            }%", style = TextStyle(
//                                color = Color.Red, shadow = Shadow(
//                                    color = Color.DarkGray,
//                                    offset = Offset(1.0f, 1.0f),
//                                    blurRadius = 3f
//                                )
//                            ), fontSize = 12.sp
//                        )
//                    }
//                    Column(
//                        horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Row {
//                            Text(
//                                text = "=",
//                                fontFamily = FontFamily.Cursive,
//                                fontWeight = FontWeight.W900,
//                                color = MaterialTheme.colors.secondary,
//                            )
//                            Text(
//                                text = "₹${dish.count * dish.price.removePrefix("₹").toDouble()}",
//                                fontFamily = FontFamily.Cursive,
//                                fontWeight = FontWeight.Medium,
//                                color = MaterialTheme.colors.onSurface,
//                            )
//                        }
//                    }
//                }
//            }
//        }
//        if (removeConfirm) {
//            AlertDialog(
//                onDismissRequest = { removeConfirm = false },
//                title = { Text("Confirm Removal") },
//                text = { Text("Are you sure you want to remove \n${dish.name} ?") },
//                confirmButton = {
//                    Button(onClick = {
//                        dish.count = 0
//                        cartItems.remove(dish)
//                        updateTotals()
//                        saveCartItemsToSharedPreferences()
//                        Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show()
//                        removeConfirm = false
//                    }) {
//                        Text("Remove")
//                    }
//                },
//                dismissButton = {
//                    Button(onClick = { removeConfirm = false }) {
//                        Text("Cancel")
//                    }
//                }
//            )
//        }
//    }
//    Divider(
//        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
//        color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
//        thickness = 1.dp
//    )
//}