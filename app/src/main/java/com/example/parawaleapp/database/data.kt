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
var img by  mutableStateOf<Uri?>(null)

// FOR CART
var count by mutableStateOf(0)
var total by  mutableStateOf(0)
var cartItems: MutableList<Dishfordb> = mutableListOf()

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

fun getCartItemsFromSharedPreferences(context: Context){
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    // Retrieve the JSON string from SharedPreferences
    val cartItemsJson = sharedPreferences.getString("cartItems", "")

    count = sharedPreferences.getString("count", "")?.toInt() ?: 0
    total = sharedPreferences.getString("total", "")?.toInt() ?: 0
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





fun totalcount(){
    total = 0
    for (i in cartItems){
        val priceWithoutCurrency = i.price.removePrefix("₹")
        val priceAsDouble = priceWithoutCurrency.toDoubleOrNull()

        if (priceAsDouble != null) {
            total += (i.count * priceAsDouble).toInt()
        }
    }
}



fun countItems(){
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

















/*data class Dish(
    val name: String,
    val price: String,
    var count: Int,
    val description: String,
    val category: String,
    val image: Int
)




val Dishes = listOf(
    Dish(
        "Fortune Refined Oil(1ltr)",
        "₹110",
        0,
        "fulfills the body's needs for Omega 3 fatty acids Soy Oil...",
        "Oil",
        R.drawable.fortune110
    ),
    Dish(
        "PatanJali Cow Ghee(500ml)",
        "₹355",
        0,
        "Patanjali Cow's Ghee is made from the milk of indigenous cows. It is...",
        "Oil",
        R.drawable.patanjali355
    ),
    Dish(
        "Tata Salt(1kg)",
        "₹18",
        0,
        "Tata Salt is one of the most recognizable brands in India. Tata Salt...",
        "Spices",
        R.drawable.tatasalt18
    ),
    Dish(
        "Daawat Rozana Gold(5kg)",
        "₹350",
        0,
        "Daawat Rozana Gold is the finest Basmati Rice in the mid-price...",
        "Rice",
        R.drawable.daawatrozanasuper399
    ),
    Dish(
        "Tata Sampann Chana Dal(1kg)",
        "₹118.75",
        0,
        "Tata Sampann Unpolished Chana Dal is made from 100% unpolished...",
        "Pulses",
        R.drawable.tatachanadaal118
    ),
    Dish(
        "Mejestic- Cake Rusk | Extra Soft | 350g",
        "₹240",
        0,
        "Majestic Cake Rusk is a delicious and crunchy snack that is...",
        "Grocery",
        R.drawable.mejestirusk240
    ),
    Dish(
        "Happilo Almonds 500g",
        "₹339",
        0,
        "100% Natural Premium California Dried Almonds are a great source of protein...",
        "DryFruits",
        R.drawable.happilo339
    ),
    Dish(
        "Nutraj Walnut Kernels(2 X 250g)",
        "₹499",
        0,
        "Nutraj California Walnuts are a great source of protein, fibre...",
        "DryFruits",
        R.drawable.nutralwallnut498
    ),
    Dish(
        "Nutraj Long Raisin 500g",
        "₹175",
        0,
        "| Kishmish |Super Rich in Iron & Vitamin B | Seedless Green Kishmish | Healthy Snacks | Dry Fruits",
        "DryFruits",
        R.drawable.nutrajkismis175
    )
)*/




data class Slidess(
    val Type: String,
    val Description: String,
    val image: Int,
)

val SlidesItems = listOf(
    Slidess(
        "Cart",
        "see items added to cart",
        R.drawable.ig_cart
    ),
    Slidess(
        "Wishlist",
        "see items added to wishlist",
        R.drawable.wishheart
    ),
    Slidess(
        "Orders",
        "see your previous orders",
        R.drawable.order_cardboard
    ),
    Slidess(
        "Manage Account",
        "edit your account details",
        R.drawable.edit_account_logo
    ),
    Slidess(
        "Settings",
        "Notification, Language",
        R.drawable.setting
    ),
    Slidess(
        "Add Items",
        "Add items to database",
        R.drawable.additemcloud
    )


)

