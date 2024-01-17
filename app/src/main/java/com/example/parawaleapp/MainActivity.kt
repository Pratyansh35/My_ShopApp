package com.example.parawaleapp

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.parawaleapp.database.restoreDataFromSharedPreferences
import com.example.parawaleapp.mainScreen.AddItemScreen
import com.example.parawaleapp.mainScreen.AddItems
import com.example.parawaleapp.mainScreen.AfterCart
import com.example.parawaleapp.mainScreen.Cart
import com.example.parawaleapp.mainScreen.CartDrawerPanel
import com.example.parawaleapp.mainScreen.ConfirmCart
import com.example.parawaleapp.mainScreen.Home
import com.example.parawaleapp.mainScreen.HomeScreen
import com.example.parawaleapp.mainScreen.LeftDrawerPanel
import com.example.parawaleapp.mainScreen.Location
import com.example.parawaleapp.mainScreen.LocationScreen
import com.example.parawaleapp.mainScreen.Login
import com.example.parawaleapp.mainScreen.Menu
import com.example.parawaleapp.mainScreen.MenuListScreen
import com.example.parawaleapp.mainScreen.NavBar
import com.example.parawaleapp.mainScreen.ProfileSet
import com.example.parawaleapp.mainScreen.Profileset
import com.example.parawaleapp.sign_in.GoogleAuthUiclient
import com.example.parawaleapp.sign_in.SignInViewModel
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
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {

                FirebaseApp.initializeApp(this)
                restoreDataFromSharedPreferences(this)
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()

                NavHost(navController = navController, startDestination = "sign_in") {
                    composable("sign_in") {
                        val viewModel = viewModel<SignInViewModel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        LaunchedEffect(key1 = Unit ){
                            if (googleAuthUiClient.getSinedInUser() != null) {
                                navController.navigate("MainScreen")
                            }
                        }

                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult(),
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
                                    applicationContext,
                                    "Sign in successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("MainScreen")
                                viewModel.resetState()
                            }
                        }

                        SignInScreen(
                            state = state,
                            onSignInClick = {
                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthUiClient.signIn()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            }
                        )
                    }
                    composable("MainScreen") {
                        MainScreen(navController,googleAuthUiClient = googleAuthUiClient, scope = scope)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController2: NavController,googleAuthUiClient: GoogleAuthUiclient, scope: CoroutineScope) {
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val context = LocalContext.current
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            // Left drawer content
            LeftDrawerPanel(scaffoldState = scaffoldState,
                scope = scope,
                navController = navController,
                userData = googleAuthUiClient.getSinedInUser(),
                signOut = {
                    scope.launch {
                        googleAuthUiClient.signOut()
                        Toast.makeText(
                            context ,
                            "Sign out successful",
                            Toast.LENGTH_LONG
                        ).show()
                        }
                    navController2.navigate("sign_in")

                })

        }, drawerGesturesEnabled = true,

        topBar = {
            NavBar(
                scaffoldState = scaffoldState,
                scope = scope,
                navController = navController
            )
        },
        bottomBar = { MyBottomNavigation(navController = navController) }) {

        Box(Modifier.padding(it)) {
            NavHost(navController = navController, startDestination = Home.route) {

                composable(Home.route) {
                    HomeScreen()
                }
                composable(Menu.route) {
                    MenuListScreen()
                }

                composable(Location.route) {
                    LocationScreen()
                }
                composable(Cart.route) {
                    CartDrawerPanel(navController = navController)
                }
                composable(AfterCart.route) {
                    ConfirmCart()
                }
                composable(ProfileSet.route) {
                    Profileset(userData = googleAuthUiClient.getSinedInUser())
                }
                composable(AddItems.route){
                    AddItemScreen()
                }
            }
        }
    }
}


/*@Composable
fun Login(){
    var isUserLoggedIn by remember { mutableStateOf(false) }

    if (isUserLoggedIn) {
        // Navigate to another Composable (MyApp).
        MyApp()
    } else {
        LoginScreen(onLoginSuccess = {
            isUserLoggedIn = true
        })
    }
}*/


/*@Composable
fun MyApp(googleAuthUiClient: GoogleAuthUiclient) {
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,

        drawerContent = {
            // Left drawer content
            LeftDrawerPanel(scaffoldState = scaffoldState, scope = scope,navController = navController,
                userData = googleAuthUiClient.getSinedInUser(),
                signOut = {
                    lifecycleScope

            })

            }, drawerGesturesEnabled = true,

        topBar = { NavBar(scaffoldState = scaffoldState, scope = scope,navController = navController) },
        bottomBar = { MyBottomNavigation(navController = navController) }) {

        Box(Modifier.padding(it)) {
            NavHost(navController = navController, startDestination = Home.route) {

                composable(Home.route) {
                    HomeScreen()
                }
                composable(Menu.route) {
                    MenuListScreen()
                }

                composable(Location.route) {
                    LocationScreen()
                }
                composable(Cart.route) {
                    CartDrawerPanel(navController = navController)
                }
                composable(AfterCart.route){
                    ConfirmCart()
                }
                composable(ProfileSet.route){
                    Profileset()
                }
            }
        }
    }
}*/




@Composable
fun MyBottomNavigation(navController: NavController) {
    val destinationList = listOf(
        Home,
        Menu,
        Location
    )
    val selectedIndex = rememberSaveable {
        mutableStateOf(0)
    }
    BottomNavigation {
        destinationList.forEachIndexed { index, destination ->
            BottomNavigationItem(
                label = { Text(text = destination.title) },
                icon = {
                    Icon(
                        painter = painterResource(id = destination.icon),
                        contentDescription = destination.title
                    )
                },
                selected = index == selectedIndex.value,
                onClick = {
                    selectedIndex.value = index
                    navController.navigate(destinationList[index].route) {
                        popUpTo(Login.route)
                        launchSingleTop = true
                    }
                })
        }
    }
}