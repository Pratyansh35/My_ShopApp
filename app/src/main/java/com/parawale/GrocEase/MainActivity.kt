package com.parawale.GrocEase

import AddItems
import AfterCart
import AppLayout
import BluetoothScreenRoute
import Cart
import Home
import LocationSelectionScreen
import Login
import Menu
import PreviousOrders
import ProfileSet
import Scan_Barcode
import SettingScreen
import User_Location
import ViewOrder
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.parawale.GrocEase.Ai.ItemSelectionPopup
import com.parawale.GrocEase.AppLayout.AppLayoutScreen
import com.parawale.GrocEase.ClothesScreen.ClothesScreen
import com.parawale.GrocEase.DataClasses.Dishfordb
import com.parawale.GrocEase.DataClasses.UserAddressDetails
import com.parawale.GrocEase.DataClasses.UserData
import com.parawale.GrocEase.ElectronicScreen.ElectronicHomePage
import com.parawale.GrocEase.Location.AdditionalDetailsScreen
import com.parawale.GrocEase.Location.FetchCurrentLocation
import com.parawale.GrocEase.Location.UserAddressScreen
import com.parawale.GrocEase.Location.getLocations
import com.parawale.GrocEase.Location.uploadLocation
import com.parawale.GrocEase.MedicinesScreen.MedicinesScreen
import com.parawale.GrocEase.Notifications.createNotificationChannel
import com.parawale.GrocEase.PaymentUpi.PaymentScreenLayout
import com.parawale.GrocEase.SendViewOrders.OrderDetailsScreen
import com.parawale.GrocEase.SendViewOrders.PersonOrdersScreen
import com.parawale.GrocEase.SendViewOrders.ViewOrders
import com.parawale.GrocEase.ViewModels.SharedViewModel
import com.parawale.GrocEase.barcodeScreen.BarCodeScreen
import com.parawale.GrocEase.cartScreen.CartDrawerPanel
import com.parawale.GrocEase.cartScreen.ConfirmCart
import com.parawale.GrocEase.cartScreen.PreviousOrders
import com.parawale.GrocEase.database.ManageItem
import com.parawale.GrocEase.database.clearDataFromSharedPreferences
import com.parawale.GrocEase.database.getAllDishes
import com.parawale.GrocEase.database.getUserFromSharedPreferences
import com.parawale.GrocEase.database.saveUserToSharedPreferences
import com.parawale.GrocEase.drawerPanel.leftPanel.LeftDrawerPanel
import com.parawale.GrocEase.drawerPanel.leftPanel.Profileset
import com.parawale.GrocEase.drawerPanel.leftPanel.Settings
import com.parawale.GrocEase.mainScreen.HomeScreen
import com.parawale.GrocEase.mainScreen.ItemDescription
import com.parawale.GrocEase.mainScreen.MenuListScreen
import com.parawale.GrocEase.mainScreen.NavBar
import com.parawale.GrocEase.printer.BluetoothScreen
import com.parawale.GrocEase.sign_in.GoogleAuthUiClient
import com.parawale.GrocEase.sign_in.SignInScreen
import com.parawale.GrocEase.sign_in.SignInState
import com.parawale.GrocEase.sign_in.SignInViewModel
import com.parawale.GrocEase.ui.theme.MyAppTheme
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.parawale.GrocEase.DataClasses.LiveLocation
import com.parawale.GrocEase.DataClasses.Merchant
import com.parawale.GrocEase.DataClasses.MerchantAddress
import com.parawale.GrocEase.database.forMerchants.fetchMerchantItems
import com.parawale.GrocEase.database.forMerchants.getAllMerchants
import com.parawale.GrocEase.mainScreen.diffLayouts.MerchantCard
import com.parawale.GrocEase.mainScreen.diffLayouts.MerchantsGrid
//import com.parawale.GrocEase.mainScreen.diffLayouts.MerchantsList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        createNotificationChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }

        setContent {
            MyApp()
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun MyApp() {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
        ) {
            val isSystemDark = isSystemInDarkTheme()

            var isDarkTheme by rememberSaveable { mutableStateOf(isSystemDark) }
            var dishData by rememberSaveable { mutableStateOf<List<Dishfordb>>(emptyList()) }
            val navController = rememberNavController()
            val context = LocalContext.current


            var userData by remember { mutableStateOf<UserData?>(null) }

            Log.d("MainActivity userData", "User data: $userData")
            userData = getUserFromSharedPreferences(context)
            val userState = remember { mutableStateOf(userData) }
            val insetsController = WindowInsetsControllerCompat(window, window.decorView)


            LaunchedEffect(isSystemDark) {
                isDarkTheme = isSystemDark
            }
            LaunchedEffect(isDarkTheme) {
                window.statusBarColor = if (isDarkTheme)   Color(0xFF000000).toArgb() else Color(0xFFFFFFFF).toArgb()
                insetsController.isAppearanceLightStatusBars = !isDarkTheme
            }

            LaunchedEffect(key1 = userState.value) {
                if (userState.value != null) {
                    dishData = getAllDishes().orEmpty()
                    Toast.makeText(
                        applicationContext,
                        "Welcome back, ${userState.value?.userName}",
                        Toast.LENGTH_SHORT
                    ).show()

                    navController.navigate("MerchantScreen") {
                        popUpTo("sign_in") { inclusive = true }
                    }
                }
            }

            NavHost(
                navController = navController,
                startDestination = if (userState.value != null) "MerchantScreen" else "sign_in"
            ) {

                composable("sign_in") { SignIn(navController, onDishDataChange = { dishData = it }) }

                composable("MerchantScreen"){
                    val merchants = remember { mutableStateOf<List<Merchant>>(emptyList()) }

                    var backPressedTime by remember { mutableLongStateOf(0L) }
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
                    LaunchedEffect(Unit) {
                            val fetchedMerchants = getAllMerchants()
                            merchants.value = fetchedMerchants
                    }

                    MerchantsGrid(
                        merchants = merchants.value,
                        onViewItemsClick = { merchant ->
                            navController.navigate("MainScreen/${merchant}")
                        }
                    )
                }
                composable("MainScreen/{merchant}"){ it ->
                    val viewModel = viewModel<SignInViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    val items = remember { mutableStateOf<List<Dishfordb>>(emptyList()) }
                    val merchant = it.arguments?.getString("merchant")

                    LaunchedEffect(merchant) {
                        items.value = fetchMerchantItems(merchant.toString())

                    }
                    MainScreen(
                        navController,
                        googleAuthUiClient = googleAuthUiClient,
                        dishData = items.value,
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    checkNotificationPermission()
                }

                Toast.makeText(
                    applicationContext, "Sign in successful", Toast.LENGTH_SHORT
                ).show()

                onDishDataChange(getAllDishes())
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

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
    var allOverTotalPrice by rememberSaveable { mutableDoubleStateOf(0.0) }
    var allOverTotalMrp by rememberSaveable { mutableDoubleStateOf(0.0) }
    var isGridLayout by rememberSaveable { mutableStateOf(false) }

    var userData by remember { mutableStateOf<UserData?>(null) }

    val sharedViewModel: SharedViewModel = viewModel()
    var isMapInteracting by remember { mutableStateOf(false) }

    var selectedAddress by remember { mutableStateOf<UserAddressDetails?>(null) }

    LaunchedEffect(Unit) {
        userData = googleAuthUiClient.getSignedInUser()
        userData?.let { user ->
            if (user.userAddressDetails.isNullOrEmpty()) {
                val fetchedAddresses = getLocations(user)
                if (fetchedAddresses.isNotEmpty()) {
                    userData = user.copy(userAddressDetails = fetchedAddresses)
                }
            }
            Log.d("FirebaseData", "Retrieved locations: ${userData?.userAddressDetails}")
        }
    }


    fun updateTotals() {
        allOverTotalPrice = cartItems.sumOf { it.price.removePrefix("₹").toDouble() * it.count }
        allOverTotalMrp = cartItems.sumOf { it.mrp.removePrefix("₹").toDouble() * it.count }
    }

    MyAppTheme(darkTheme = isDark) {
        Scaffold(
            scaffoldState = scaffoldState,

            drawerContent = {
                LeftDrawerPanel(
                    scaffoldState = scaffoldState,
                    scope = scope,
                    navController = innerNavController,
                    userData = userData,
                    signOut = {
                        clearDataFromSharedPreferences(context)
                        scope.launch {
                            googleAuthUiClient.signOut(email = userData?.userEmail)
                            Toast.makeText(context, "Sign out successful", Toast.LENGTH_LONG).show()
                            navController.navigate("sign_in") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                )
            },
            drawerGesturesEnabled = !isMapInteracting,
            topBar = {
                NavBar(
                    scaffoldState = scaffoldState,
                    scope = scope,
                    navController = innerNavController,
                    isDarkTheme = isDark,
                    count = cartItems.size
                )
            },
            bottomBar = { MyBottomNavigation(navController = innerNavController, userData ) }
        ) { paddingValues ->
            Box(Modifier.padding(paddingValues)) {
                NavHost(navController = innerNavController, startDestination = Home.route) {
                    composable(Home.route) {
                        HomeScreen(
                            dishData = dishData,
                            isDarkTheme = isDark,
                            onThemeChange = { isDarkTheme(it) },
                            cartItems = cartItems,
                            updateTotals = ::updateTotals,
                            navController = innerNavController,
                            isGridLayout = isGridLayout,
                            onLayoutChange = { isGridLayout = it },
                            context = context,
                            sharedViewModel = sharedViewModel
                        )
                    }
                    composable("itemSelection") {
                        val items = sharedViewModel.selectedItems.value
                        Log.d("itemSelection", "items: $items")
                        ItemSelectionPopup(
                            suggestions = items,
                            cartItems = cartItems,
                            updateTotals = ::updateTotals,
                            navController = innerNavController,
                            onDismiss = { innerNavController.popBackStack() }
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
                            userData = userData,
                            cartItems = cartItems,
                            total = allOverTotalPrice,
                            totalmrp = allOverTotalMrp,
                            selectedAddress = selectedAddress
                        )
                    }
                    composable(ProfileSet.route) {
                        Profileset(userData = userData, onSendVerificationCodeClick = onSendVerificationCodeClick,
                            linkWithOtpClick = linkWithOtpClick)
                    }
                    composable(AddItems.route) {
                        ManageItem(
                            userData = userData, dishData = dishData
                        )
                    }
                    composable(SettingScreen.route) {
                        Settings(
                            scope = scope,
                            navController = innerNavController,
                            userData = userData,
                            scaffoldState = scaffoldState
                        )
                    }
                    composable(BluetoothScreenRoute.route) { BluetoothScreen() }
                    composable(ViewOrder.route) { ViewOrders(navController = innerNavController) }
                    composable(AppLayout.route) { AppLayoutScreen() }



                    composable("Location") {
//                        ShopDistanceScreen(
//                            shopLocation = LatLng(
//                                27.099407,
//                                83.271651
//                            )
//                        )
                    }
                    composable(PreviousOrders.route) {
                        PreviousOrders(innerNavController, userData)
                    }
                    composable("PaymentScreen/{totalMrp}/{totalValue}") { backStackEntry ->
                        val totalMrp = backStackEntry.arguments?.getString("totalMrp")?.toDouble()
                        val totalValue =
                            backStackEntry.arguments?.getString("totalValue")?.toDouble()
                        if (totalMrp != null && totalValue != null) {
                            PaymentScreenLayout(
                                totalMrp,
                                totalValue,
                                userData,
                                cartItems,
                                isDark,
                                state = state,
                                linkWithOtpClick = linkWithOtpClick,
                                onSendVerificationCodeClick = onSendVerificationCodeClick,
                                location = selectedAddress
                            )
                        }
                    }

                    composable("mapScreen"){
                        var currentLocation  by remember { mutableStateOf<Location?>(null) }
                        FetchCurrentLocation(context = context) { currentLocation = it }

                        LocationSelectionScreen(
                            userData = userData,
                            currentLocation = currentLocation,
                            onCurrentLocationClicked = {},
                            onAddAddressClicked = {
                                innerNavController.navigate("user_location")
                            },
                            onAddressSelected = {
                                selectedAddress = it
                                Toast.makeText(context, "Selected $selectedAddress", Toast.LENGTH_SHORT).show()
                                Log.d("mapScreen", "Selected address: $selectedAddress")
                            },
                            onEditAddressClicked = {})
                    }
                    composable(User_Location.route) {
                        UserAddressScreen(
                            innerNavController,
                            onMapInteractionStart = { isMapInteracting = true },
                            onMapInteractionEnd = { isMapInteracting = false }
                        )
                    }
                    composable("confirmAddress/{userAddress}") { backStackEntry ->
                        val userAddressJson = backStackEntry.arguments?.getString("userAddress")
                        val userAddress = Gson().fromJson(userAddressJson, UserAddressDetails::class.java)
                        Log.d("UserAddressScreen", "confirmScreen $userAddress")
                        AdditionalDetailsScreen(
                            userData = userData,
                            userAddressDetails = userAddress,
                            onSave = {
                                uploadLocation(userData!!, it.userAddressDetails)
                                userData = it
                                Log.d("UserAddressScreen", "Updated user data: $userData")
                            },
                            navController = innerNavController,
                            selectedLocation = {selectedAddress = it}
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
                        val loggedemail = userData?.userEmail

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
                    composable("Electronics"){
                        val electronicItems by remember { derivedStateOf { dishData.filter { it.mainCategory.equals("Electronics", true) } } }
                        ElectronicHomePage(
                            electronicItems = electronicItems,
                            cartItems = cartItems,
                            updateTotals = ::updateTotals,
                            navController = innerNavController,
                            onItemClick = {  }
                        )
                    }
                    composable("Apparels"){
                        val clothesItems by remember { derivedStateOf { dishData.filter { it.mainCategory.equals("Apparel", true) } } }

                        ClothesScreen(
                            clothesItems = clothesItems,
                            cartItems = cartItems,
                            updateTotals = ::updateTotals,
                            navController = innerNavController,
                            onItemClick = {  }
                        )
                    }
                    composable("Medicines"){
                        val medicinesItems by remember { derivedStateOf { dishData.filter { it.mainCategory.equals("Medicines", true) } } }
                        MedicinesScreen(
                            medicinesItems = medicinesItems,
                            cartItems = cartItems,
                            updateTotals = ::updateTotals,
                            navController = innerNavController,
                            onItemClick = {  }
                        )
                    }
                    composable("Groceries"){
                        val grocItems by remember { derivedStateOf { dishData.filter { it.mainCategory.equals("Groceries", true) } } }
                        MenuListScreen(
                            grocItems,
                            cartItems = cartItems,
                            updateTotals = ::updateTotals,
                            navController = innerNavController
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun MyBottomNavigation(navController: NavController, userData: UserData?) {

    val destinationList = listOf(
        Home, Menu, if (userData?.isAdmin == true) Scan_Barcode else null , User_Location
    )
    Log.d("MyBottomNavigation", "Destination List: $userData")

    val selectedIndex = rememberSaveable {
        mutableIntStateOf(0)
    }
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        contentColor = MaterialTheme.colors.surface
    ) {
        destinationList.forEachIndexed { index, destination ->
            if (destination != null) {
                BottomNavigationItem(label = { Text(text = destination.title) }, icon = {
                    Icon(
                        painter = painterResource(id = destination.icon),
                        contentDescription = destination.title
                    )
                }, selected = index == selectedIndex.intValue, onClick = {
                    selectedIndex.intValue = index
                    destinationList[index]?.let {
                        navController.navigate(it.route) {
                            popUpTo(Login.route)
                            launchSingleTop = true
                        }
                    }
                })
            }
        }
    }
}