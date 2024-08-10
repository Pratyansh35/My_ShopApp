package com.example.parawaleapp

import AddItems
import AfterCart
import AppLayout
import BluetoothScreenRoute
import Cart
import Home
import Login
import Menu
import PreviousOrders
import ProfileSet
import Scan_Barcode
import SettingScreen
import ShopDistanceScreen
import User_Location
import ViewOrder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parawaleapp.AppLayout.AppLayoutScreen
import com.example.parawaleapp.Location.AdditionalDetailsScreen
import com.example.parawaleapp.Location.UserAddressScreen
import com.example.parawaleapp.Notifications.createNotificationChannel
import com.example.parawaleapp.PaymentUpi.PaymentScreenLayout
import com.example.parawaleapp.SendViewOrders.OrderDetailsScreen
import com.example.parawaleapp.SendViewOrders.PersonOrdersScreen
import com.example.parawaleapp.SendViewOrders.ViewOrders
import com.example.parawaleapp.barcodeScreen.BarCodeScreen
import com.example.parawaleapp.cartScreen.CartDrawerPanel
import com.example.parawaleapp.cartScreen.ConfirmCart
import com.example.parawaleapp.cartScreen.PreviousOrders
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.database.ManageItem
import com.example.parawaleapp.database.clearDataFromSharedPreferences
import com.example.parawaleapp.database.getUserFromSharedPreferences
import com.example.parawaleapp.database.getdishes
import com.example.parawaleapp.database.saveUserToSharedPreferences
import com.example.parawaleapp.drawerPanel.leftPanel.LeftDrawerPanel
import com.example.parawaleapp.drawerPanel.leftPanel.Profileset
import com.example.parawaleapp.drawerPanel.leftPanel.Settings
import com.example.parawaleapp.mainScreen.HomeScreen
import com.example.parawaleapp.mainScreen.ItemDescription
import com.example.parawaleapp.mainScreen.MenuListScreen
import com.example.parawaleapp.mainScreen.NavBar
import com.example.parawaleapp.printer.BluetoothScreen
import com.example.parawaleapp.sign_in.GoogleAuthUiClient
import com.example.parawaleapp.sign_in.SignInScreen
import com.example.parawaleapp.sign_in.SignInState
import com.example.parawaleapp.sign_in.SignInViewModel
import com.example.parawaleapp.ui.theme.MyAppTheme
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        createNotificationChannel(this)
        setContent {
            MyApp()
        }
    }

    @Composable
    fun MyApp() {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
        ) {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            var dishData by rememberSaveable { mutableStateOf<List<Dishfordb>>(emptyList()) }
            val navController = rememberNavController()
            val context = LocalContext.current

            val userData = getUserFromSharedPreferences(context)
            val userState = remember { mutableStateOf(userData) }

            LaunchedEffect(key1 = userState.value) {
                if (userState.value != null) {
                    dishData = getdishes("Items").orEmpty()
                    Toast.makeText(
                        applicationContext,
                        "Welcome back, ${userState.value?.userName}",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.navigate("MainScreen") {
                        popUpTo("sign_in") { inclusive = true }
                    }
                }
            }

            NavHost(
                navController = navController,
                startDestination = if (userState.value != null) "MainScreen" else "sign_in"
            ) {
                composable("sign_in") { SignIn(navController, onDishDataChange = { dishData = it }) }
                composable("MainScreen") {
                    val viewModel = viewModel<SignInViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    MainScreen(
                        navController,
                        googleAuthUiClient = googleAuthUiClient,
                        dishData = dishData,
                        isDarkTheme = { isDarkTheme = it },
                        isDark = isDarkTheme,
                        state = state,
                        linkWithOtpClick = { verificationCode ->
                            viewModel.linkPhoneNumberWithOtp(verificationCode)
                        },
                        onSendVerificationCodeClick = { phoneNumber ->
                            viewModel.sendVerificationCode(this@MainActivity, phoneNumber)
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun SignIn(navController: NavController, onDishDataChange: (List<Dishfordb>) -> Unit) {
        val viewModel = viewModel<SignInViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current
        LaunchedEffect(key1 = state.isSignInSuccessful) {
            if (state.isSignInSuccessful) {
                Toast.makeText(
                    applicationContext, "Sign in successful", Toast.LENGTH_SHORT
                ).show()
                getdishes("Items")?.let { newData ->
                    onDishDataChange(newData)
                }
                saveUserToSharedPreferences(context, state.userData)
                navController.navigate("MainScreen") {
                    popUpTo("sign_in") { inclusive = true }
                }
                viewModel.resetState()
            }
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                lifecycleScope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        result.data ?: return@launch
                    )
                    viewModel.onSignInResult(signInResult)
                }
            } else {
                viewModel.stopLoading()
            }
        }

        SignInScreen(state = state, onSignInClick = { activity, phoneNumber ->
            viewModel.sendVerificationCode(activity, phoneNumber)
        }, onGoogleSignInClick = {
            viewModel.startLoading()
            lifecycleScope.launch {
                val signInIntentSender = googleAuthUiClient.signIn()
                launcher.launch(
                    IntentSenderRequest.Builder(
                        signInIntentSender ?: return@launch
                    ).build()
                )
            }
        }, onVerifyCodeClick = { verificationCode ->
            viewModel.verifyPhoneNumberWithCode(verificationCode)
        })
    }
}

@Composable
fun MainScreen(
    navController: NavController,
    googleAuthUiClient: GoogleAuthUiClient,
    dishData: List<Dishfordb>,
    isDarkTheme: (Boolean) -> Unit,
    isDark: Boolean,
    state: SignInState,
    linkWithOtpClick: (String) -> Unit,
    onSendVerificationCodeClick: (String) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val innerNavController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cartItems = remember { mutableStateListOf<Dishfordb>() }
    var allOverTotalPrice by rememberSaveable { mutableStateOf(0.0) }
    var allOverTotalMrp by rememberSaveable { mutableStateOf(0.0) }
    var isGridLayout by rememberSaveable { mutableStateOf(false) }

    fun updateTotals() {
        allOverTotalPrice = cartItems.sumOf { it.price.removePrefix("₹").toDouble() * it.count }
        allOverTotalMrp = cartItems.sumOf { it.mrp.removePrefix("₹").toDouble() * it.count }
    }
    val systemInDarkTheme = isSystemInDarkTheme()
    LaunchedEffect(systemInDarkTheme) {
        isDarkTheme(systemInDarkTheme)
    }

    MyAppTheme(darkTheme = isDark) {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerContent = {
                LeftDrawerPanel(
                    scaffoldState = scaffoldState,
                    scope = scope,
                    navController = innerNavController,
                    userData = googleAuthUiClient.getSignedInUser(),
                    signOut = {
                        clearDataFromSharedPreferences(context)
                        scope.launch {
                            googleAuthUiClient.signOut(email = googleAuthUiClient.getSignedInUser()?.userEmail)
                            Toast.makeText(context, "Sign out successful", Toast.LENGTH_LONG).show()
                            navController.navigate("sign_in")
                        }
                    }
                )
            },
            drawerGesturesEnabled = true,
            topBar = {
                NavBar(
                    scaffoldState = scaffoldState,
                    scope = scope,
                    navController = innerNavController,
                    isDarkTheme = isDark,
                    count = cartItems.size
                )
            },
            bottomBar = { MyBottomNavigation(navController = innerNavController) }
        ) { paddingValues ->
            Box(Modifier.padding(paddingValues)) {
                NavHost(navController = innerNavController, startDestination = Home.route) {
                    composable(Home.route) {
                        HomeScreen(
                            DishData = dishData,
                            isDarkTheme = isDark,
                            onThemeChange = { isDarkTheme(it) },
                            cartItems = cartItems,
                            updateTotals = ::updateTotals,
                            navController = innerNavController,
                            isGridLayout = isGridLayout,
                            onLayoutChange = { isGridLayout = it }
                        )
                    }
                    composable(Menu.route) {
                        MenuListScreen(
                            dishData,
                            cartItems = cartItems,
                            updateTotals = ::updateTotals,
                            navController = innerNavController
                        )
                    }
                    composable(Scan_Barcode.route) {
                        BarCodeScreen(
                            dishData,
                            cartItems = cartItems,
                            updateTotals = ::updateTotals
                        )
                    }
                    composable(Cart.route) {
                        CartDrawerPanel(
                            navController = innerNavController,
                            cartItems = cartItems,
                            allOverTotalPrice = allOverTotalPrice,
                            updateTotals = ::updateTotals
                        )
                    }
                    composable(AfterCart.route) {
                        ConfirmCart(
                            navController = innerNavController,
                            userData = googleAuthUiClient.getSignedInUser(),
                            cartItems = cartItems,
                            total = allOverTotalPrice,
                            totalmrp = allOverTotalMrp
                        )
                    }
                    composable(ProfileSet.route) {
                        Profileset(userData = googleAuthUiClient.getSignedInUser(), onSendVerificationCodeClick = onSendVerificationCodeClick,
                            linkWithOtpClick = linkWithOtpClick)
                    }
                    composable(AddItems.route) {
                        ManageItem(
                            userData = googleAuthUiClient.getSignedInUser(), dishData = dishData
                        )
                    }
                    composable(SettingScreen.route) {
                        Settings(
                            scope = scope,
                            navController = innerNavController,
                            userData = googleAuthUiClient.getSignedInUser(),
                            scaffoldState = scaffoldState
                        )
                    }
                    composable(BluetoothScreenRoute.route) { BluetoothScreen() }
                    composable(ViewOrder.route) { ViewOrders(navController = innerNavController) }
                    composable(AppLayout.route) { AppLayoutScreen() }



                    composable("Location") {
                        ShopDistanceScreen(
                            shopLocation = LatLng(
                                27.099407,
                                83.271651
                            ) // Example shop location (Delhi, India)
                        )
                    }
                    composable(PreviousOrders.route) {
                        PreviousOrders(navController, googleAuthUiClient.getSignedInUser())
                    }
                    composable("PaymentScreen/{totalMrp}/{totalValue}") { backStackEntry ->
                        val totalMrp = backStackEntry.arguments?.getString("totalMrp")?.toDouble()
                        val totalValue =
                            backStackEntry.arguments?.getString("totalValue")?.toDouble()
                        if (totalMrp != null && totalValue != null) {
                            PaymentScreenLayout(
                                totalMrp,
                                totalValue,
                                googleAuthUiClient.getSignedInUser(),
                                cartItems,
                                isDark,
                                state = state,
                                linkWithOtpClick = linkWithOtpClick,
                                onSendVerificationCodeClick = onSendVerificationCodeClick
                            )
                        }
                    }
                    composable(User_Location.route) {
                        UserAddressScreen(onConfirm = { latLng, address ->
                                innerNavController.navigate("confirmAddress")
                        })
                    }
                    composable("confirmAddress"){
                        var home by remember { mutableStateOf("Home") }
                        var apartment by remember { mutableStateOf("Apartment") }
                        var landmark by remember { mutableStateOf("Landmark") }
                        var notes by remember { mutableStateOf("Notes") }
                        AdditionalDetailsScreen( home = home, apartment = apartment, landmark = landmark, notes = notes,
                            onSave = { home, apartment, landmark, notes ->

                            }
                        )
                    }
                    composable("personOrders/{email}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email")
                        PersonOrdersScreen(innerNavController, email)
                    }

                    composable("itemDescription/{dish}") { backStackEntry ->
                        val dishJson = backStackEntry.arguments?.getString("dish")
                        val item = Gson().fromJson(dishJson, Dishfordb::class.java)
                        if (item != null) {
                            ItemDescription(
                                item,
                                cartItems = cartItems,
                                updateTotals = ::updateTotals
                            )
                        }
                    }
                    composable("orderDetails/{email}/{date}/{name}/{orderedItems}/{timeStamp}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email")
                        val date = backStackEntry.arguments?.getString("date")
                        val name = backStackEntry.arguments?.getString("name")
                        val orderedItemsJson = backStackEntry.arguments?.getString("orderedItems")
                        val orderTimeStamp = backStackEntry.arguments?.getString("timeStamp")
                        val loggedemail = googleAuthUiClient.getSignedInUser()?.userEmail

                        if (orderedItemsJson != null && orderTimeStamp != null) {

                            OrderDetailsScreen(
                                navController = innerNavController,
                                email = email,
                                date = date,
                                name = name,
                                loggedUser = loggedemail,
                                orderedItemsJson = orderedItemsJson,
                                timeStamp = orderTimeStamp
                            )

                        }
                    }

                }
            }
        }
    }
}



@Composable
fun MyBottomNavigation(navController: NavController) {

    val destinationList = listOf(
        Home, Menu, Scan_Barcode, User_Location
    )
    val selectedIndex = rememberSaveable {
        mutableIntStateOf(0)
    }
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        contentColor = MaterialTheme.colors.surface
    ) {
        destinationList.forEachIndexed { index, destination ->
            BottomNavigationItem(label = { Text(text = destination.title) }, icon = {
                Icon(
                    painter = painterResource(id = destination.icon),
                    contentDescription = destination.title
                )
            }, selected = index == selectedIndex.intValue, onClick = {
                selectedIndex.intValue = index
                navController.navigate(destinationList[index].route) {
                    popUpTo(Login.route)
                    launchSingleTop = true
                }
            })
        }
    }
}



//
//class MainActivity : ComponentActivity() {
//    private val googleAuthUiClient by lazy {
//        GoogleAuthUiClient(
//            context = applicationContext,
//            oneTapClient = Identity.getSignInClient(applicationContext)
//        )
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        FirebaseApp.initializeApp(this)
//        createNotificationChannel(this)
//        setContent {
//            Surface(
//                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
//            ) {
//                var isDarkTheme by remember { mutableStateOf(false) }
//                val navController = rememberNavController()
//                var dishData by rememberSaveable { mutableStateOf<List<Dishfordb>>(emptyList()) }
//                val context = LocalContext.current
//                val userData = getUserFromSharedPreferences(context)
//                if (userData == null) {
//                    Log.d("SavingUser", "No user data found in SharedPreferences")
//                } else {
//                    Log.d("SavingUser", "User data loaded: $userData")
//                }
//
//                // Update userState and navigate based on savedUser
//                val userState = remember { mutableStateOf(userData) }
//
//                LaunchedEffect(key1 = Unit) {
//                    if (userState.value != null) {
//                        getdishes("Items")?.let { newData -> dishData = newData }
//                        Toast.makeText(
//                            applicationContext,
//                            "Welcome back, ${userState.value?.userName}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        Log.d("MainActivity", "Navigating to MainScreen")
//                        navController.navigate("MainScreen") {
//                            popUpTo("sign_in") { inclusive = true }
//                        }
//                    } else {
//                        Log.d("MainActivity", "User not signed in, navigating to sign_in screen")
//                    }
//                }
//
//                NavHost(
//                    navController = navController,
//                    startDestination = if (userState.value != null) "MainScreen" else "sign_in"
//                ) {
//                    composable("sign_in") {
//                        val viewModel = viewModel<SignInViewModel>()
//                        val state by viewModel.state.collectAsStateWithLifecycle()
//
//                        LaunchedEffect(key1 = state.isSignInSuccessful) {
//                            if (state.isSignInSuccessful) {
//                                Toast.makeText(
//                                    applicationContext, "Sign in successful", Toast.LENGTH_SHORT
//                                ).show()
//                                saveUserToSharedPreferences(context, state.userData)
//                                getdishes("Items")?.let { newData -> dishData = newData }
//                                navController.navigate("MainScreen") {
//                                    popUpTo("sign_in") { inclusive = true }
//                                }
//                                viewModel.resetState()
//                            }
//                        }
//
//                        val launcher = rememberLauncherForActivityResult(
//                            contract = ActivityResultContracts.StartIntentSenderForResult()
//                        ) { result ->
//                            if (result.resultCode == RESULT_OK) {
//                                lifecycleScope.launch {
//                                    val signInResult = googleAuthUiClient.signInWithIntent(
//                                        result.data ?: return@launch
//                                    )
//                                    viewModel.onSignInResult(signInResult)
//                                }
//                            } else {
//                                viewModel.stopLoading()
//                            }
//                        }
//
//                        SignInScreen(state = state, onSignInClick = { activity, phoneNumber ->
//                            viewModel.sendVerificationCode(
//                                activity, phoneNumber
//                            )
//                        }, onGoogleSignInClick = {
//                            viewModel.startLoading()
//                            lifecycleScope.launch {
//                                val signInIntentSender = googleAuthUiClient.signIn()
//                                launcher.launch(
//                                    IntentSenderRequest.Builder(
//                                        signInIntentSender ?: return@launch
//                                    ).build()
//                                )
//                            }
//                        }, onVerifyCodeClick = { verificationCode ->
//                            viewModel.verifyPhoneNumberWithCode(verificationCode)
//                        },
////                            onSendVerificationCodeClick = { phoneNumber ->
////                            viewModel.sendVerificationCode(this@MainActivity, phoneNumber) }
//                        )
//                    }
//                    composable("MainScreen") {
//                        val viewModel = viewModel<SignInViewModel>()
//                        val state by viewModel.state.collectAsStateWithLifecycle()
//                        MainScreen(
//                            navController,
//                            googleAuthUiClient = googleAuthUiClient,
//                            dishData = dishData,
//                            scope = rememberCoroutineScope(),
//                            state = state,
//                            onVerifyCodeClick = { verificationCode ->
//                                viewModel.verifyPhoneNumberWithCode(verificationCode)
//                            }, onSendVerificationCodeClick = { phoneNumber ->
//                                viewModel.sendVerificationCode(this@MainActivity, phoneNumber)
//                            },
//                            isDarkTheme = { isDarkTheme = it },
//                            isDark = isDarkTheme
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//fun MainScreen(
//    navController2: NavController,
//    googleAuthUiClient: GoogleAuthUiClient,
//    scope: CoroutineScope,
//    dishData: List<Dishfordb>,
//    state: SignInState,
//    onVerifyCodeClick: (String) -> Unit,
//    onSendVerificationCodeClick: (String) -> Unit,
//    isDarkTheme: (Boolean) -> Unit,
//    isDark: Boolean
//) {
//    val scaffoldState = rememberScaffoldState()
//    val navController = rememberNavController()
//    val context = LocalContext.current
//
//    val cartItems = remember { mutableStateListOf<Dishfordb>() }
//    var allOverTotalPrice by rememberSaveable { mutableDoubleStateOf(0.0) }
//    var allOverTotalMrp by rememberSaveable { mutableDoubleStateOf(0.0) }
//
//    fun updateTotals() {
//        allOverTotalPrice = cartItems.sumOf { it.price.removePrefix("₹").toDouble() * it.count }
//        allOverTotalMrp = cartItems.sumOf { it.mrp.removePrefix("₹").toDouble() * it.count }
//    }
//
//    // { Todo add getCartItemsFromSharedPreferences() }
////    fun saveCartItemsToSharedPreferences() {
////        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
////        val editor = sharedPreferences.edit()
////        val gson = Gson()
////        val cartItemsJson = gson.toJson(cartItems)
////        editor.putString("cartItems", cartItemsJson)
////        editor.apply()
////    }
//
//
//
//    val systemInDarkTheme = isSystemInDarkTheme()
//    LaunchedEffect(systemInDarkTheme) {
//        isDarkTheme(systemInDarkTheme)
//    }
//
//    var isGridLayout by rememberSaveable { mutableStateOf(false) }
//
//    MyAppTheme(darkTheme = isDark) {
//        Scaffold(scaffoldState = scaffoldState, drawerContent = {
//            LeftDrawerPanel(scaffoldState = scaffoldState,
//                scope = scope,
//                navController = navController,
//                userData = googleAuthUiClient.getSignedInUser(),
//                signOut = {
//                    clearDataFromSharedPreferences(context)
//                    scope.launch {
//                        googleAuthUiClient.signOut()
//                        Toast.makeText(context, "Sign out successful", Toast.LENGTH_LONG).show()
//                        navController2.navigate("sign_in")
//                    }
//                })
//        }, drawerGesturesEnabled = true, topBar = {
//            NavBar(
//                scaffoldState = scaffoldState,
//                scope = scope,
//                navController = navController,
//                isDarkTheme = isDark,
//                count = cartItems.size
//            )
//        }, bottomBar = { MyBottomNavigation(navController = navController) }) { it ->
//            Box(Modifier.padding(it)) {
//                NavHost(navController = navController, startDestination = Home.route) {
//                    composable(Home.route) {
//                        HomeScreen(DishData = dishData,
//                            isDarkTheme = isDark,
//                            onThemeChange = { isDarkTheme(it) },
//                            cartItems = cartItems,
//                            updateTotals = ::updateTotals,
//                            //saveCartItemsToSharedPreferences = ::saveCartItemsToSharedPreferences,
//                            navController = navController,
//                            isGridLayout = isGridLayout,
//                            onLayoutChange = { isGridLayout = it })
//                    }
//                    composable(Menu.route) {
//                        MenuListScreen(
//                            dishData,
//                            cartItems = cartItems,
//                            updateTotals = ::updateTotals,
//                            //saveCartItemsToSharedPreferences = ::saveCartItemsToSharedPreferences,
//                            navController = navController
//                        )
//                    }
//                    composable(Scan_Barcode.route) {
//                        BarCodeScreen(
//                            dishData,
//                            cartItems = cartItems,
//                            updateTotals = ::updateTotals,
//                           // saveCartItemsToSharedPreferences = ::saveCartItemsToSharedPreferences
//                        )
//                    }
//                    composable(Cart.route) {
//                        CartDrawerPanel(
//                            navController = navController,
//                            cartItems = cartItems,
//                            allOverTotalPrice = allOverTotalPrice,
//                            updateTotals = ::updateTotals,
//                            //saveCartItemsToSharedPreferences = ::saveCartItemsToSharedPreferences
//                        )
//                    }
//                    composable(AfterCart.route) {
//                        ConfirmCart(
//                            navController = navController,
//                            userData = googleAuthUiClient.getSignedInUser(),
//                            cartItems = cartItems,
//                            total = allOverTotalPrice,
//                            totalmrp = allOverTotalMrp
//                        )
//                    }
//                    composable(ProfileSet.route) {
//                        Profileset(userData = googleAuthUiClient.getSignedInUser())
//                    }
//                    composable(AddItems.route) {
//                        ManageItem(
//                            userData = googleAuthUiClient.getSignedInUser(), dishData = dishData
//                        )
//                    }
//                    composable(SettingScreen.route) {
//                        Settings(
//                            scope = scope,
//                            navController = navController,
//                            userData = googleAuthUiClient.getSignedInUser(),
//                            scaffoldState = scaffoldState
//                        )
//                    }
//                    composable(BluetoothScreenRoute.route) { BluetoothScreen() }
//                    composable(ViewOrder.route) { ViewOrders(navController = navController) }
//                    composable(AppLayout.route) {
//                        AppLayoutScreen()
//                    }
//
//                    composable("personOrders/{email}") { backStackEntry ->
//                        val email = backStackEntry.arguments?.getString("email")
//                        PersonOrdersScreen(navController, email)
//                    }
//                    composable("itemDescription/{dish}") { backStackEntry ->
//                        val dishJson = backStackEntry.arguments?.getString("dish")
//                        val item = Gson().fromJson(dishJson, Dishfordb::class.java)
//                        if (item != null) {
//                            ItemDescription(
//                                item,
//                                cartItems = cartItems,
//                                updateTotals = ::updateTotals,
//                               // saveCartItemsToSharedPreferences = ::saveCartItemsToSharedPreferences
//                            )
//                        }
//                    }
//                    composable("orderDetails/{email}/{date}/{name}/{orderedItems}/{timeStamp}") { backStackEntry ->
//                        val email = backStackEntry.arguments?.getString("email")
//                        val date = backStackEntry.arguments?.getString("date")
//                        val name = backStackEntry.arguments?.getString("name")
//                        val orderedItemsJson = backStackEntry.arguments?.getString("orderedItems")
//                        val orderTimeStamp = backStackEntry.arguments?.getString("timeStamp")
//                        val loggedemail = googleAuthUiClient.getSignedInUser()?.userEmail
//
//                        if (orderedItemsJson != null && orderTimeStamp != null) {
//
//                            OrderDetailsScreen(
//                                navController = navController,
//                                email = email,
//                                date = date,
//                                name = name,
//                                loggedUser = loggedemail,
//                                orderedItemsJson = orderedItemsJson,
//                                timeStamp = orderTimeStamp
//                            )
//
//                        }
//                    }
//                    composable("Location") {
//                        ShopDistanceScreen(
//                            shopLocation = LatLng(
//                                27.099407,
//                                83.271651
//                            ) // Example shop location (Delhi, India)
//                        )
//                    }
//                    composable(PreviousOrders.route) {
//                        PreviousOrders(navController, googleAuthUiClient.getSignedInUser())
//                    }
//                    composable("PaymentScreen/{totalMrp}/{totalValue}") { backStackEntry ->
//                        val totalMrp = backStackEntry.arguments?.getString("totalMrp")?.toDouble()
//                        val totalValue =
//                            backStackEntry.arguments?.getString("totalValue")?.toDouble()
//                        if (totalMrp != null && totalValue != null) {
//                            PaymentScreenLayout(
//                                totalMrp,
//                                totalValue,
//                                googleAuthUiClient.getSignedInUser(),
//                                cartItems,
//                                isDark,
//                                state = state,
//                                onVerifyCodeClick = onVerifyCodeClick,
//                                onSendVerificationCodeClick = onSendVerificationCodeClick
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}