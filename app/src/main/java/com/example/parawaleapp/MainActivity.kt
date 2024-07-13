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
import ViewOrder
import android.content.Context
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.parawaleapp.AppLayout.AppLayoutScreen
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
import com.example.parawaleapp.database.getdishes
import com.example.parawaleapp.database.restoreDataFromSharedPreferences
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
import com.example.parawaleapp.sign_in.SignInViewModel
import com.example.parawaleapp.ui.theme.MyAppTheme
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
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
        restoreDataFromSharedPreferences(this)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
            ) {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                var dishData by rememberSaveable { mutableStateOf<List<Dishfordb>>(emptyList()) }

                NavHost(navController = navController, startDestination = "sign_in") {
                    composable("sign_in") {
                        val viewModel = viewModel<SignInViewModel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        LaunchedEffect(key1 = Unit) {
                            if (googleAuthUiClient.getSinedInUser() != null || Firebase.auth.currentUser != null) {
                                getdishes("Items")?.let { newData ->
                                    dishData = newData
                                }
                                navController.navigate("MainScreen")
                            }
                        }

                        val launcher =
                            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if (result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    } else {
                                        viewModel.stopLoading() // Stop loading when login is cancelled
                                    }
                                })

                        LaunchedEffect(key1 = state.isSignInSuccessful) {
                            if (state.isSignInSuccessful && state.isPhoneNumberLinked) {
                                Toast.makeText(
                                    applicationContext, "Sign in successful", Toast.LENGTH_SHORT
                                ).show()
                                getdishes("Items")?.let { newData ->
                                    dishData = newData
                                }
                                navController.navigate("MainScreen")
                                viewModel.resetState()
                            }
                        }

                        DisposableEffect(key1 = navController.currentBackStackEntryAsState().value) {
                            onDispose {
                                viewModel.stopLoading() // Stop loading when navigating away
                            }
                        }

                        SignInScreen(
                            state = state,
                            onSignInClick = { activity, phoneNumber -> viewModel.sendVerificationCode(activity, phoneNumber) },
                            onGoogleSignInClick = {
                                viewModel.startLoading()
                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthUiClient.signIn()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            },
                            onVerifyCodeClick = { verificationCode ->
                                viewModel.verifyPhoneNumberWithCode(verificationCode)
                            }
                        )

                    }
                    composable("MainScreen") {
                        MainScreen(
                            navController,
                            googleAuthUiClient = googleAuthUiClient,
                            scope = scope,
                            dishData
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MainScreen(
    navController2: NavController,
    googleAuthUiClient: GoogleAuthUiClient,
    scope: CoroutineScope,
    dishData: List<Dishfordb>
) {
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val context = LocalContext.current

    val cartItems = remember { mutableStateListOf<Dishfordb>() }

    var allOverTotalPrice by rememberSaveable { mutableDoubleStateOf(0.0) }
    var allOverTotalMrp by rememberSaveable { mutableDoubleStateOf(0.0) }

    fun updateTotals() {
        allOverTotalPrice = cartItems.sumOf { it.price.removePrefix("₹").toDouble() * it.count }
        allOverTotalMrp = cartItems.sumOf { it.mrp.removePrefix("₹").toDouble() * it.count }
    }

    fun saveCartItemsToSharedPreferences() {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        val gson = Gson()
        val cartItemsJson = gson.toJson(cartItems)

        editor.putString("cartItems", cartItemsJson)
        editor.putString("total", allOverTotalPrice.toString())
        editor.apply()
    }

    var isDarkTheme by remember { mutableStateOf(false) }

    val systemInDarkTheme = isSystemInDarkTheme()
    LaunchedEffect(systemInDarkTheme) {
        isDarkTheme = systemInDarkTheme
    }

    var isGridLayout by rememberSaveable { mutableStateOf(false) }

    MyAppTheme(darkTheme = isDarkTheme) {
        Scaffold(scaffoldState = scaffoldState, drawerContent = {
            LeftDrawerPanel(scaffoldState = scaffoldState,
                scope = scope,
                navController = navController,
                userData = googleAuthUiClient.getSinedInUser(),
                signOut = {
                    clearDataFromSharedPreferences(context)
                    scope.launch {
                        googleAuthUiClient.signOut()
                        Toast.makeText(context, "Sign out successful", Toast.LENGTH_LONG).show()
                        navController2.navigate("sign_in")
                    }
                })
        }, drawerGesturesEnabled = true, topBar = {
            NavBar(
                scaffoldState = scaffoldState,
                scope = scope,
                navController = navController,
                isDarkTheme = isDarkTheme,
                count = cartItems.size
            )
        }, bottomBar = { MyBottomNavigation(navController = navController) }) { it ->
            Box(Modifier.padding(it)) {
                NavHost(navController = navController, startDestination = Home.route) {
                    composable(Home.route) {
                        HomeScreen(
                            DishData = dishData,
                            isDarkTheme = isDarkTheme,
                            onThemeChange = { isDarkTheme = it },
                            cartItems = cartItems,
                            updateTotals = ::updateTotals,
                            saveCartItemsToSharedPreferences = ::saveCartItemsToSharedPreferences,
                            navController = navController,
                            isGridLayout = isGridLayout,
                            onLayoutChange = { isGridLayout = it }
                        )
                    }
                    composable(Menu.route) {
                        MenuListScreen(
                            dishData,
                            cartItems = cartItems,
                            updateTotals = ::updateTotals,
                            saveCartItemsToSharedPreferences = ::saveCartItemsToSharedPreferences,
                            navController = navController
                        )
                    }
                    composable(Scan_Barcode.route) {
                        BarCodeScreen(
                            dishData,
                            cartItems = cartItems,
                            updateTotals = ::updateTotals,
                            saveCartItemsToSharedPreferences = ::saveCartItemsToSharedPreferences
                        )
                    }
                    composable(Cart.route) {
                        CartDrawerPanel(
                            navController = navController,
                            cartItems = cartItems,
                            allOverTotalPrice = allOverTotalPrice,
                            updateTotals = ::updateTotals,
                            saveCartItemsToSharedPreferences = ::saveCartItemsToSharedPreferences
                        )
                    }
                    composable(AfterCart.route) {
                        ConfirmCart(
                            navController = navController,
                            userData = googleAuthUiClient.getSinedInUser(),
                            cartItems = cartItems,
                            total = allOverTotalPrice,
                            totalmrp = allOverTotalMrp
                        )
                    }
                    composable(ProfileSet.route) {
                        Profileset(userData = googleAuthUiClient.getSinedInUser())
                    }
                    composable(AddItems.route) {
                        ManageItem(
                            userData = googleAuthUiClient.getSinedInUser(), dishData = dishData
                        )
                    }
                    composable(SettingScreen.route) {
                        Settings(
                            scope = scope,
                            navController = navController,
                            userData = googleAuthUiClient.getSinedInUser(),
                            scaffoldState = scaffoldState
                        )
                    }
                    composable(BluetoothScreenRoute.route) { BluetoothScreen() }
                    composable(ViewOrder.route) { ViewOrders(navController = navController) }
                    composable(AppLayout.route) {
                        AppLayoutScreen()
                    }

                    composable("personOrders/{email}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email")
                        PersonOrdersScreen(navController, email)
                    }
                    composable("itemDescription/{dish}") { backStackEntry ->
                        val dishJson = backStackEntry.arguments?.getString("dish")
                        val item = Gson().fromJson(dishJson, Dishfordb::class.java)
                        if (item != null) {
                            ItemDescription(
                                item,
                                cartItems = cartItems,
                                updateTotals = ::updateTotals,
                                saveCartItemsToSharedPreferences = ::saveCartItemsToSharedPreferences
                            )
                        }
                    }
                    composable("orderDetails/{email}/{date}/{name}/{orderedItems}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email")
                        val date = backStackEntry.arguments?.getString("date")
                        val name = backStackEntry.arguments?.getString("name")
                        val orderedItemsJson = backStackEntry.arguments?.getString("orderedItems")
                        val loggedemail = googleAuthUiClient.getSinedInUser()?.userEmail
                        OrderDetailsScreen(
                            navController, email, date, name, loggedemail, orderedItemsJson
                        )
                    }
                    composable("Location"){
                        ShopDistanceScreen(
                            shopLocation = LatLng(  27.099407, 83.271651) // Example shop location (Delhi, India)
                        )
                    }
                    composable(PreviousOrders.route) {
                        PreviousOrders(navController, googleAuthUiClient.getSinedInUser())
                    }
                    composable("PaymentScreen/{totalMrp}/{totalValue}") { backStackEntry ->
                        val totalMrp = backStackEntry.arguments?.getString("totalMrp")?.toDouble()
                        val totalValue =
                            backStackEntry.arguments?.getString("totalValue")?.toDouble()
                        if (totalMrp != null && totalValue != null) {
                            PaymentScreenLayout(
                                totalMrp,
                                totalValue,
                                googleAuthUiClient.getSinedInUser(),
                                cartItems,
                                isDarkTheme
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
        Home, Menu, Scan_Barcode
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