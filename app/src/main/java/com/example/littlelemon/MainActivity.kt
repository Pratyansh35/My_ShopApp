package com.example.littlelemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                Login()
        }
        }
    }



@Composable
fun Login(){
    var isUserLoggedIn by remember { mutableStateOf(false) }

    if (isUserLoggedIn) {
        // Navigate to another Composable (MyApp).
        MyApp()
    } else {
        LoginScreen(onLoginSuccess = {
            // This block is executed when the login is successful.
            isUserLoggedIn = true
        })
    }
}



@Composable
fun MyApp() {
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,

        drawerContent = {
            // Left drawer content
            LeftDrawerPanel(scaffoldState = scaffoldState, scope = scope)

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
            }
        }
    }
}




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