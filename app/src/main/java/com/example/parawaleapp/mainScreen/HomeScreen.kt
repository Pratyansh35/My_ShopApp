package com.example.parawaleapp.mainScreen


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.parawaleapp.R
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.ui.theme.MyAppTheme
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@Composable
fun HomeScreen(
    DishData: List<Dishfordb>,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    cartItems: SnapshotStateList<Dishfordb>,
    updateTotals: () -> Unit,
    saveCartItemsToSharedPreferences: () -> Unit,
    navController: NavController,
    isGridLayout: Boolean,
    onLayoutChange: (Boolean) -> Unit
) {
    MyAppTheme(darkTheme = isDarkTheme) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Column {
                    SlidingPanels()
                    WeeklySpecial(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = onThemeChange,
                        isGridLayout = isGridLayout,
                        onLayoutChange = onLayoutChange
                    )
                }
            }
            if (!isGridLayout) {
                items(DishData) { dish ->
                    LinearLayoutItems(
                        dish,
                        cartItems = cartItems,
                        updateTotals = updateTotals,
                        saveCartItemsToSharedPreferences = saveCartItemsToSharedPreferences,
                        navController = navController
                    )
                }
            } else {
                items(DishData.chunked(3)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        for (dish in rowItems) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                            ) {
                                GridLayoutItems(
                                    dish = dish,
                                    cartItems = cartItems,
                                    updateTotals = updateTotals,
                                    saveCartItemsToSharedPreferences = saveCartItemsToSharedPreferences,
                                    navController = navController
                                )
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SlidingPanels() {
    val pagerState = rememberPagerState(initialPage = 0, initialPageOffsetFraction = 0f, pageCount = { 2 })
    val context = LocalContext.current

    // Auto-scroll logic
    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(6000) // Delay between page transitions
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> UpperPanel()
                1 -> UpperPanel2()
            }
        }

        // Page indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(4.dp)
        ) {
            repeat(pagerState.pageCount) { page ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(width = 16.dp, height = 8.dp)
                        .background(
                            color = if (pagerState.currentPage == page) MaterialTheme.colors.primary else Color.Gray,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun UpperPanel() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .height(300.dp)
            .fillMaxSize()
            .background(MaterialTheme.colors.secondary)
            .padding(16.dp)
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
fun UpperPanel2() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .height(300.dp)
            .background(MaterialTheme.colors.secondary)
            .padding(16.dp)
    ) {
        Text(
            text = "क्यों ना बढ़े हमभी" ,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Get Your Own App",
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Medium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))
        BulletPoint(text = "Make your business online")
        BulletPoint(text = "Easy delivery with Map live tracking")
        BulletPoint(text = "Barcode system")
        Row {
            Column {
                BulletPoint(text = "Easy billing via Bluetooth")
                BulletPoint(text = "All backend data handling")
            }
            Column (modifier = Modifier.fillMaxWidth()

            ){
                Button(
                    onClick = {
                        // open dialer and dial number 7007254934
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:7007254934")
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .align(Alignment.End),

                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                ) {
                    Text(
                        text = "Contact",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
        BulletPoint(text = "and Many more features")

    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        verticalAlignment = CenterVertically,
        modifier = Modifier.padding(bottom = 2.dp)
    ) {
        Text(
            text = "\u2022", // Unicode for bullet point
            fontSize = 14.sp,
            color = MaterialTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colors.onSurface
        )
    }
}




@Composable
fun WeeklySpecial(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    isGridLayout: Boolean,
    onLayoutChange: (Boolean) -> Unit
) {
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
            IconButton(onClick = { onLayoutChange(!isGridLayout) }) {
                val iconhori =
                    if (isDarkTheme) R.drawable.horizontalayoutlight else R.drawable.horizontalayoutdark
                val icongrid =
                    if (isDarkTheme) R.drawable.gridlayoutlight else R.drawable.gridlayoutdark
                val icon = if (isGridLayout) iconhori else icongrid
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = if (isGridLayout) "Grid View" else "Linear View",
                    modifier = Modifier.size(24.dp)
                )
            }
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
fun GridLayoutItems(
    dish: Dishfordb,
    cartItems: SnapshotStateList<Dishfordb>,
    updateTotals: () -> Unit,
    saveCartItemsToSharedPreferences: () -> Unit,
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
                if (discountPercentage > 0)
                {
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
                            saveCartItemsToSharedPreferences()

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
    dish: Dishfordb,
    cartItems: SnapshotStateList<Dishfordb>,
    updateTotals: () -> Unit,
    saveCartItemsToSharedPreferences: () -> Unit,
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
                            saveCartItemsToSharedPreferences()

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
                AsyncImage(model = Uri.parse(dish.imagesUrl[0]),
                    contentDescription = "dishImage",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            val dishJson = gson.toJson(dish)
                            navController.navigate("itemDescription/${Uri.encode(dishJson)}")
                        },
                    )
                if (discountPercentage > 0){
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


@Composable
fun ItemDescription(
    dish: Dishfordb,
    cartItems: SnapshotStateList<Dishfordb>,
    updateTotals: () -> Unit,
    saveCartItemsToSharedPreferences: () -> Unit
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var cartDish = cartItems.find { it.name == dish.name }
    var dishcount by remember {
        mutableIntStateOf(cartDish?.count ?: 0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 70.dp) // Add bottom padding to prevent content from being covered by the button
        ) {
            // Image row
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
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = dish.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(start = 12.dp)
                )
                Text(
                    text = dish.description,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 5.dp, start = 12.dp)
                )

                Text(
                    text = dish.weight,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 5.dp, start = 12.dp)
                )

                Row {
                    Text(
                        text = dish.price,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(start = 12.dp, end = 8.dp),
                        fontSize = 20.sp
                    )
                    Text(
                        text = "MRP ",
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                    Text(
                        text = dish.mrp,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        textDecoration = TextDecoration.LineThrough,
                        fontSize = 16.sp
                    )
                    Text(
                        text = " ${
                            "%.2f".format(
                                ((dish.mrp.trimStart('₹').toFloat() - dish.price.trimStart('₹')
                                    .toFloat()) / dish.mrp.trimStart('₹').toFloat()) * 100
                            )
                        }% off", color = Color(0xFF449C44), fontSize = 16.sp
                    )
                }
            }
        }
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
                    dishcount = cartDish!!.count
                }
                updateTotals()
                saveCartItemsToSharedPreferences()

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
                .align(Alignment.BottomCenter)
                .padding(10.dp)
                .fillMaxWidth()
                .height(50.dp),
        ) {
            Text(
                text = if (dishcount == 0) "Add to Cart" else "Add More $dishcount",
                color = MaterialTheme.colors.onPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
