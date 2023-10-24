package com.example.littlelemon

interface Destinations {
    val route: String
    val icon: Int
    val title: String
}


object Login : Destinations {
    override val route = "Login"
    override val icon = R.drawable.ic_launcher_foreground
    override val title = "Login"
}
object Home : Destinations {
    override val route = "Home"
    override val icon = R.drawable.ic_home
    override val title = "Home"
}
object Cart : Destinations {
    override val route = "Cart"
    override val icon = R.drawable.ig_cart
    override val title = "Cart"
}
object Menu : Destinations {
    override val route = "Menu"
    override val icon = R.drawable.ic_menu
    override val title = "Menu"
}

object Location : Destinations {
    override val route = "Location"
    override val icon = R.drawable.ic_location
    override val title = "Location"
}





