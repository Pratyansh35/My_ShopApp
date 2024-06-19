package com.example.parawaleapp

import AddItems
import AfterCart
import BluetoothScreenRoute
import Cart
import Home
import Login
import Menu
import ProfileSet
import Scan_Barcode
import ViewOrder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.parawaleapp.mainScreen.HomeScreen
import com.example.parawaleapp.mainScreen.MenuListScreen
import com.example.parawaleapp.mainScreen.NavBar
import com.example.parawaleapp.printer.BluetoothScreen
import com.example.parawaleapp.sign_in.GoogleAuthUiclient
import com.example.parawaleapp.sign_in.SignInViewModel
import com.example.parawaleapp.sign_in.UserData
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiclient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
            ) {

                FirebaseApp.initializeApp(this)
                restoreDataFromSharedPreferences(this)
                //getCartItemsFromSharedPreferences(this)
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                var dishData by remember { mutableStateOf<List<Dishfordb>>(emptyList()) }

                NavHost(navController = navController, startDestination = "sign_in") {
                    composable("sign_in") {
                        val viewModel = viewModel<SignInViewModel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        LaunchedEffect(key1 = Unit) {
                            if (googleAuthUiClient.getSinedInUser() != null) {
                                // Fetch dish data // edited for item reload after restart
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
                                    }

                                })
                        LaunchedEffect(key1 = state.isSignInSuccessful) {
                            if (state.isSignInSuccessful) {
                                Toast.makeText(
                                    applicationContext, "Sign in successful", Toast.LENGTH_SHORT
                                ).show()
                                // Fetch dish data // edited for item reload after sign in
                                getdishes("Items")?.let { newData ->
                                    dishData = newData
                                }
                                navController.navigate("MainScreen")
                                viewModel.resetState()
                            }
                        }

                        SignInScreen(state = state, onSignInClick = {
                            lifecycleScope.launch {
                                val signInIntentSender = googleAuthUiClient.signIn()
                                launcher.launch(
                                    IntentSenderRequest.Builder(
                                        signInIntentSender ?: return@launch
                                    ).build()
                                )
                            }
                        })
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
    googleAuthUiClient: GoogleAuthUiclient,
    scope: CoroutineScope,
    dishData: List<Dishfordb>
) {
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val context = LocalContext.current

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
        NavBar(scaffoldState = scaffoldState, scope = scope, navController = navController)
    }, bottomBar = { MyBottomNavigation(navController = navController) }) {
        Box(Modifier.padding(it)) {
            NavHost(navController = navController, startDestination = Home.route) {
                composable(Home.route) { HomeScreen(dishData) }
                composable(Menu.route) { MenuListScreen(dishData) }
                composable(Scan_Barcode.route) { BarCodeScreen(dishData) }
                composable(Cart.route) { CartDrawerPanel(navController = navController) }
                composable(AfterCart.route) {
                    ConfirmCart(
                        navController = navController,
                        userData = googleAuthUiClient.getSinedInUser()
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
                composable(BluetoothScreenRoute.route) { BluetoothScreen() }
                composable(ViewOrder.route) { ViewOrders(navController = navController) }
                composable("personOrders/{email}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    PersonOrdersScreen(navController, email)
                }
                composable("orderDetails/{email}/{date}/{name}/{orderedItems}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    val date = backStackEntry.arguments?.getString("date")
                    val name = backStackEntry.arguments?.getString("name")
                    val orderedItemsJson = backStackEntry.arguments?.getString("orderedItems")
                    val loggedemail = googleAuthUiClient.getSinedInUser()?.userEmail
                    OrderDetailsScreen(navController, email, date, name, loggedemail, orderedItemsJson)
                }
                composable(PreviousOrders.route) {
                    PreviousOrders(navController, googleAuthUiClient.getSinedInUser())
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
        mutableStateOf(0)
    }
    BottomNavigation {
        destinationList.forEachIndexed { index, destination ->
            BottomNavigationItem(label = { Text(text = destination.title) }, icon = {
                Icon(
                    painter = painterResource(id = destination.icon),
                    contentDescription = destination.title
                )
            }, selected = index == selectedIndex.value, onClick = {
                selectedIndex.value = index
                navController.navigate(destinationList[index].route) {
                    popUpTo(Login.route)
                    launchSingleTop = true
                }
            })
        }
    }
}