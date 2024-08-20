package com.example.parawaleapp.mainScreen


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.parawaleapp.Ai.fetchSuggestionsFromOpenAI
import com.example.parawaleapp.R
import com.example.parawaleapp.ViewModels.SharedViewModel
import com.example.parawaleapp.VoiceRequests.startListening
import com.example.parawaleapp.VoiceRequests.stopListening
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.mainScreen.DiffLayouts.GridLayoutItems
import com.example.parawaleapp.mainScreen.DiffLayouts.LinearLayoutItems
import com.example.parawaleapp.mainScreen.UpperPanels.UpperPanel
import com.example.parawaleapp.mainScreen.UpperPanels.UpperPanel2
import com.example.parawaleapp.mainScreen.UpperPanels.UpperPanel3
import com.example.parawaleapp.ui.theme.MyAppTheme
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun HomeScreen(
    dishData: List<Dishfordb>,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    cartItems: SnapshotStateList<Dishfordb>,
    updateTotals: () -> Unit,
    navController: NavController,
    isGridLayout: Boolean,
    onLayoutChange: (Boolean) -> Unit,
    context: Context,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    sharedViewModel: SharedViewModel
) {

    var backPressedTime by remember { mutableLongStateOf(0L) }
    var voiceInputResult by remember { mutableStateOf("") } // To store the voice input result
    val suggestions by remember { mutableStateOf(emptyList<Dishfordb>()) } // To store the suggestions

    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime > 2000) {
            Toast.makeText(context, "Press again to exit", Toast.LENGTH_SHORT).show()
            backPressedTime = currentTime
        } else {
            context as Activity
            context.finish() // Exit the app
        }
    }
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val listening = remember { mutableStateOf(false) }
    val isPressed = remember { mutableStateOf(false) }
    if (ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as Activity, arrayOf(Manifest.permission.RECORD_AUDIO), 1
        )
    }
    MyAppTheme(darkTheme = isDarkTheme) {
        Scaffold(floatingActionButton = {
            // Gradient colors
            val gradientStartColor by animateColorAsState(
                targetValue = if (isPressed.value) Color.Blue else Color.Cyan,
                animationSpec = tween(durationMillis = 500),
                label = ""
            )
            val gradientEndColor by animateColorAsState(
                targetValue = if (isPressed.value) Color.Red else Color.Yellow,
                animationSpec = tween(durationMillis = 500),
                label = ""
            )

            Box(modifier = Modifier
                .size(60.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(gradientStartColor, gradientEndColor)
                    ), shape = CircleShape
                )
                .clickable {
                    // Handle click action
                    Log.d("VoiceInput", "Button clicked.")
                }
                .pointerInput(Unit) {
                    detectTapGestures(onPress = {
                        // User starts pressing the button
                        isPressed.value = true
                        startListening(context) { result ->
                            if (result == "error") {
                                Log.e(
                                    "VoiceInput", "Speech recognition error occurred."
                                )
                            } else {
                                voiceInputResult = result
                                Log.d("VoiceInput", "User said: $result")
                                lifecycleOwner.lifecycleScope.launch {
                                    ProcessVoiceInput(sharedViewModel,navController, context, result)
                                }
                                Toast
                                    .makeText(context, result, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        tryAwaitRelease()
                        // User releases the button
                        isPressed.value = false
                        stopListening(speechRecognizer)
                    })
                }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite, // Use a microphone icon
                    contentDescription = "Voice Input", tint = Color.White
                )
            }
        }, content = { paddingValues -> // This is the padding provided by Scaffold
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Apply the padding here
            ) {
                item {
                    Column {
                        SlidingPanels(darkTheme = isDarkTheme)
                        WeeklySpecial(
                            isDarkTheme = isDarkTheme,
                            onThemeChange = onThemeChange,
                            isGridLayout = isGridLayout,
                            onLayoutChange = onLayoutChange
                        )
                    }
                }
                if (suggestions.isNotEmpty()) {
                    items(suggestions) { dish ->
                        LinearLayoutItems(
                            dish,
                            cartItems = cartItems,
                            updateTotals = updateTotals,
                            navController = navController
                        )
                    }
                } else {
                    if (!isGridLayout) {
                        items(dishData) { dish ->
                            LinearLayoutItems(
                                dish,
                                cartItems = cartItems,
                                updateTotals = updateTotals,
                                navController = navController
                            )
                        }
                    } else {
                        items(dishData.chunked(3)) { rowItems ->
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
        })
    }
}


suspend fun ProcessVoiceInput( sharedViewModel: SharedViewModel,navController: NavController, context: Context, query: String) {
    val items = mutableListOf<Dishfordb>()
    try {
        val fetchedItems = fetchSuggestionsFromOpenAI(context, query)
        items.clear()
        if (fetchedItems != null) {
            val distinct = mutableListOf<Dishfordb>()
            for (item in fetchedItems) {
                if (!distinct.contains(item)) {
                    distinct.add(item)
                }
            }
            items.addAll(distinct)
        }
        Log.d("itemSelection", "Fetched items: $items")
        sharedViewModel.setItems(items);
        navController.navigate("itemSelection")
    } catch (e: Exception) {
        e.printStackTrace()
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SlidingPanels(darkTheme: Boolean) {
    val pagerState =
        rememberPagerState(initialPage = 0, initialPageOffsetFraction = 0f, pageCount = { 3 })
    val context = LocalContext.current
    val image =
        painterResource(id = if (darkTheme) R.drawable.upperpaneldarkbg else R.drawable.upperpanelightbg)
    // Auto-scroll logic
    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(6000) // Delay between page transitions
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .height(300.dp)
    ) {
        Image(
            painter = image,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        HorizontalPager(
            state = pagerState, modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> UpperPanel()
                1 -> UpperPanel2()
                2 -> UpperPanel3 { category ->
                    Toast.makeText(context, "Selected Category: $category", Toast.LENGTH_SHORT)
                        .show()
                }
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
fun CategoryBox(categoryName: String, iconResId: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colors.surface)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = iconResId,
                contentDescription = categoryName,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = categoryName,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colors.onSurface
            )
        }
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
fun ItemDescription(
    dish: Dishfordb,
    cartItems: SnapshotStateList<Dishfordb>,
    updateTotals: () -> Unit,
    //saveCartItemsToSharedPreferences: () -> Unit
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
