import com.parawale.GrocEase.R

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
    override val icon = R.drawable.ic_home2
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

object Scan_Barcode : Destinations {
    override val route = "Scan_Barcode"
    override val icon = R.drawable.ic_barcode
    override val title = "Barcode"
}
object User_Location : Destinations {
    override val route = "user_location"
    override val icon = R.drawable.ic_location
    override val title = "Location"
}

object ProfileSet : Destinations {
    override val route = "ProfileSet"
    override val icon = R.drawable.ic_location
    override val title = "ProfileSet"
}

object AfterCart : Destinations {
    override val route = "AfterCart"
    override val icon = R.drawable.ic_location
    override val title = "AfterCart"
}

object AddItems : Destinations {
    override val route = "AddItems"
    override val icon = R.drawable.additemcloud
    override val title = "AddItems"
}

object BluetoothScreenRoute : Destinations {
    override val route = "BluetoothScreenRoute"
    override val icon = R.drawable.setting
    override val title = "BluetoothScreenRoute"

}

object ViewOrder : Destinations {
    override val route = "viewOrders"
    override val icon = R.drawable.ic_menu
    override val title = "View Orders"
}

object PreviousOrders : Destinations {
    override val route = "PreviousOrders"
    override val icon = R.drawable.usericon
    override val title = "PreviousOrders"
}
object SettingScreen : Destinations {
    override val route = "SettingScreen"
    override val icon = R.drawable.usericon
    override val title = "SettingScreen"
}

object AppLayout : Destinations {
    override val route = "AppLayout"
    override val icon = R.drawable.usericon
    override val title = "AppLayout"
}
