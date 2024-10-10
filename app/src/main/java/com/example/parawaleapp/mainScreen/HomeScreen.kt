package com.example.parawaleapp.mainScreen


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.parawaleapp.Ai.fetchSuggestionsFromOpenAI
import com.example.parawaleapp.DataClasses.Dishfordb
import com.example.parawaleapp.Location.CurrentLocationComposable
import com.example.parawaleapp.R
import com.example.parawaleapp.ViewModels.SharedViewModel
import com.example.parawaleapp.VoiceRequests.startListening
import com.example.parawaleapp.VoiceRequests.stopListening
import com.example.parawaleapp.mainScreen.diffLayouts.GridLayoutItems
import com.example.parawaleapp.mainScreen.diffLayouts.LinearLayoutItems
import com.example.parawaleapp.mainScreen.UpperPanels.UpperPanel
import com.example.parawaleapp.mainScreen.UpperPanels.UpperPanel2
import com.example.parawaleapp.mainScreen.UpperPanels.UpperPanel3
import com.example.parawaleapp.mainScreen.diffLayouts.DishDetailsSheet
import com.example.parawaleapp.ui.theme.MyAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield


@OptIn(ExperimentalMaterialApi::class)
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
    var voiceInputResult by remember { mutableStateOf("") }
    val suggestions by remember { mutableStateOf(emptyList<Dishfordb>()) }
    var currentLocation by remember { mutableStateOf("Fetching current location...") }

    // State to handle the selected dish and modal sheet
    var selectedDish by remember { mutableStateOf<Dishfordb?>(null) }

    // Modal bottom sheet state and coroutine scope
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    // Back button handler logic
    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime > 2000) {
            Toast.makeText(context, "Press again to exit", Toast.LENGTH_SHORT).show()
            backPressedTime = currentTime
        } else {
            context as Activity
            context.finish()
        }
    }

    // Voice input and speech recognizer
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val listening = remember { mutableStateOf(false) }
    val isPressed = remember { mutableStateOf(false) }

    // Permission check for audio recording
    if (ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            1
        )
    }

    // UI
    MyAppTheme(darkTheme = isDarkTheme) {

        // ModalBottomSheetLayout wrapping the entire content of the home screen
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                // Show dish details in the bottom sheet when selected
                selectedDish?.let { dish ->
                    DishDetailsSheet(dish)
                }
            }
        ) {
            Scaffold(
                floatingActionButton = {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clickable { Log.d("VoiceInput", "Button clicked.") }
                            .pointerInput(Unit) {
                                detectTapGestures(onPress = {
                                    isPressed.value = true
                                    listening.value = true
                                    startListening(context) { result ->
                                        if (result == "error") {
                                            Log.e("VoiceInput", "Speech recognition error occurred.")
                                        } else {
                                            voiceInputResult = result
                                            Log.d("VoiceInput", "User said: $result")
                                            lifecycleOwner.lifecycleScope.launch {
                                                ProcessVoiceInput(sharedViewModel, navController, context, result)
                                            }
                                            Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    tryAwaitRelease()
                                    isPressed.value = false
                                    listening.value = false
                                    stopListening(speechRecognizer)
                                })
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            if (listening.value) {
                                ListeningAnimationBar()
                            }
                            MicrophoneWaveAnimation(isPressed = isPressed.value)
                        }
                    }
                },
                content = { paddingValues ->
                    Column(modifier = Modifier.fillMaxSize()) {

                        // LazyColumn to display dish items or suggestions
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            // Top section with location, weekly special, etc.
                            item {
                                Column {
                                    CurrentLocationComposable(navController = navController, context = context, onLocationFetched = { location ->
                                        currentLocation = location
                                    })
                                    SlidingPanels(darkTheme = isDarkTheme)
                                    WeeklySpecial(
                                        isDarkTheme = isDarkTheme,
                                        onThemeChange = onThemeChange,
                                        isGridLayout = isGridLayout,
                                        onLayoutChange = onLayoutChange
                                    )
                                }
                            }

                            // Display suggestions or regular dishData
                            if (suggestions.isNotEmpty()) {
                                items(suggestions, key = { it.id }) { dish ->
                                    LinearLayoutItems(
                                        dish = dish,
                                        cartItems = cartItems,
                                        updateTotals = updateTotals,
                                        navController = navController,
                                        onItemClick = { selectedDishItem ->
                                            selectedDish = selectedDishItem
                                            scope.launch { sheetState.show() }
                                        }
                                    )
                                }
                            } else {
                                if (!isGridLayout) {
                                    // Display items in list layout
                                    items(dishData, key = { it.id }) { dish ->
                                        LinearLayoutItems(
                                            dish = dish,
                                            cartItems = cartItems,
                                            updateTotals = updateTotals,
                                            navController = navController,
                                            onItemClick = { selectedDishItem ->
                                                selectedDish = selectedDishItem
                                                scope.launch { sheetState.show() }
                                            }
                                        )
                                    }
                                } else {
                                    // Display items in grid layout
                                    items(dishData.chunked(3), key = {
                                        it.joinToString { dish -> dish.id }
                                    }) { rowItems ->
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
                    }
                }
            )
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SlidableDishImage(
    imageUrls: List<String>,
    darkTheme: Boolean
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { imageUrls.size }
    )

    // Auto-scroll logic (optional)
    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(10000) // Delay between image transitions
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .size(108.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        // Horizontal Pager for sliding images
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // Display each image from the list
            AsyncImage(
                model = imageUrls[page],
                contentDescription = "Dish Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Page indicators (dots)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(4.dp),
            horizontalArrangement = Arrangement.Center
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
fun ListeningAnimationBar() {
    // Infinite transition for the animation
    val infiniteTransition = rememberInfiniteTransition(label = "")

    // Animations for each dot
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1.3f, animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1.3f, animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing, delayMillis = 100),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val scale3 by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1.3f, animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val scale4 by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1.3f, animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing, delayMillis = 300),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color(0xFFE0E0E0)) // Light background
    ) {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Listening...",
                color = Color.Gray,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Dot(scale = scale1, color = Color(0xFF3B3B3B)) // Dark blue dot
                Dot(scale = scale2, color = Color(0xFF4A90E2)) // Blue dot
                Dot(scale = scale3, color = Color(0xFFFFA726)) // Orange dot
                Dot(scale = scale4, color = Color(0xFFFFE082)) // Light orange dot
            }
        }
    }
}

@Composable
fun Dot(scale: Float, color: Color) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .scale(scale)
            .background(color, CircleShape)
    )
}

@Composable
fun MicrophoneWaveAnimation(
    isPressed: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    // Animate the scale for a smoother and more modern effect
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 2.5f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 2.5f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val scale3 by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 2.5f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)
    ) {
        if (isPressed) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer(scaleX = scale3, scaleY = scale3)
                    .background(Color.Blue.copy(alpha = 0.2f), shape = CircleShape)
                    .clip(CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .graphicsLayer(scaleX = scale2, scaleY = scale2)
                    .background(Color.Blue.copy(alpha = 0.4f), shape = CircleShape)
                    .clip(CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .graphicsLayer(scaleX = scale1, scaleY = scale1)
                    .background(Color.Blue.copy(alpha = 0.6f), shape = CircleShape)
                    .clip(CircleShape)
            )
        }
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Voice Input",
            tint = Color.White,
            modifier = Modifier.size(50.dp)
        )
    }
}


suspend fun ProcessVoiceInput(
    sharedViewModel: SharedViewModel,
    navController: NavController,
    context: Context,
    query: String
) {
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
        sharedViewModel.setItems(items)
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
