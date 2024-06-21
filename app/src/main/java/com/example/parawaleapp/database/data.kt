package com.example.parawaleapp.database

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.parawaleapp.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


//User Info  ImageBitmap.imageResource(R.drawable.mypic4)
var name by mutableStateOf("")
var phoneno by mutableStateOf("")
var img by mutableStateOf<Uri?>(null)

// FOR CART
var count by mutableStateOf(0)
var total by mutableStateOf(0.00)
var totalmrp by mutableStateOf(0.00)
var cartItems by mutableStateOf(mutableListOf<Dishfordb>())

fun saveDataToSharedPreferences(context: Context) {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("name", name)
        putString("phoneno", phoneno)
        putString("img", img.toString())
        apply()
    }
}

fun restoreDataFromSharedPreferences(context: Context) {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    name = sharedPreferences.getString("name", "") ?: ""
    phoneno = sharedPreferences.getString("phoneno", "") ?: ""
    val imgUriString = sharedPreferences.getString("img", "")
    img = Uri.parse(imgUriString)
}

fun saveCartItemsToSharedPreferences(context: Context) {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    // Convert the cartItems list to a JSON string
    val gson = Gson()
    val cartItemsJson = gson.toJson(cartItems)

    // Save the JSON string in SharedPreferences
    editor.putString("cartItems", cartItemsJson)
    editor.putString("count", count.toString())
    editor.putString("total", total.toString())
    editor.apply()
}

fun getCartItemsFromSharedPreferences(context: Context) {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    // Retrieve the JSON string from SharedPreferences
    val cartItemsJson = sharedPreferences.getString("cartItems", "")

    count = sharedPreferences.getString("count", "")?.toInt() ?: 0
    total = sharedPreferences.getString("total", "")?.toDouble() ?: 0.00
    // Convert the JSON string back to a MutableList<Dishfordb>
    val gson = Gson()
    val type = object : TypeToken<MutableList<Dishfordb>>() {}.type
    cartItems = gson.fromJson(cartItemsJson, type) ?: mutableListOf()
}

fun clearDataFromSharedPreferences(context: Context) {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        clear()
        apply()

    }
}


fun totalcount() {
    total = 0.0
    totalmrp = 0.00
    for (i in cartItems) {
        val priceWithoutCurrency = i.price.removePrefix("₹")
        val mrpWithoutCurrency = i.mrp.removePrefix("₹")
        val priceAsDouble = priceWithoutCurrency.toDoubleOrNull()
        val mrpAsDouble = mrpWithoutCurrency.toDoubleOrNull()
        if (priceAsDouble != null && mrpAsDouble != null) {
            total += (i.count * priceAsDouble).toInt()
            totalmrp += (i.count * mrpAsDouble).toInt()
        }
    }
}


fun countItems() {
    count = cartItems.size
}


val Categories = listOf(
    "DryFruits",
    "Rice",
    "Pulses",
    "Grocery",
    "Main",
    "Spices",
    "Oil",
)

data class Slidess(
    val Type: String,
    val Description: String,
    val image: Int,

    )



val SlidesItems = listOf(
    Slidess(
        "Cart", "see items added to cart", R.drawable.ig_cart
    ), Slidess(
        "Wishlist", "see items added to wishlist", R.drawable.wishheart
    ), Slidess(
        "Orders", "see your previous orders", R.drawable.order_cardboard
    )
//    , Slidess(
//        "Manage Account", "edit your account details", R.drawable.edit_account_logo
//    )
    , Slidess(
        "Settings", "Notification, Language", R.drawable.setting
    ), Slidess(
        "Customers Order", "See all Customers order", R.drawable.additemcloud
    ), Slidess(
        "Add Items", "Add items to database", R.drawable.additemcloud
    )
//    , Slidess(
//        "Connect Printer", "Select your bluetooth printing", R.drawable.ic_location
//    )


)

