package com.parawale.GrocEase.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.parawale.GrocEase.R
import com.parawale.GrocEase.DataClasses.UserData
import com.google.gson.Gson


//User Info  ImageBitmap.imageResource(R.drawable.mypic4)
var name by mutableStateOf("")
var phoneno by mutableStateOf("")
var img by mutableStateOf<Uri?>(null)



fun saveUserToSharedPreferences(context: Context, userData: UserData?) {
    if (userData == null) {
        Log.d("SavingUser", "No user data to save.")
        return
    }
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val gson = Gson()
    val userDataJson = gson.toJson(userData)
    editor.putString("user_data", userDataJson)
    editor.putString("name", name)
        editor.putString("phoneno", phoneno)
        editor.putString("img", img.toString())
    editor.apply()

    Log.d("SavingUser", "Saved user data to SharedPreferences: $userDataJson")
}
fun getUserFromSharedPreferences(context: Context): UserData? {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val userDataJson = sharedPreferences.getString("user_data", null)

    if (userDataJson.isNullOrEmpty()) {
        Log.d("SavingUser", "No user data found in SharedPreferences")
        return null
    }
    name = sharedPreferences.getString("name", "") ?: ""
    phoneno = sharedPreferences.getString("phoneno", "") ?: ""
    val imgUriString = sharedPreferences.getString("img", "")
    img = Uri.parse(imgUriString)
    return gson.fromJson(userDataJson, UserData::class.java).also {
        Log.d("SavingUser", "Retrieved user data: $it   name: $name    phone: $phoneno    img:  $img")
    }

}

fun clearDataFromSharedPreferences(context: Context) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        clear()
        apply()

    }
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
    val image: Int
)

val SlidesItems = listOf(
    Slidess(
        "Cart", "see items added to cart", R.drawable.ig_cart
    ), Slidess(
        "Wishlist", "see items added to wishlist", R.drawable.wishheart
    ), Slidess(
        "Orders", "see your previous orders", R.drawable.order_cardboard
    ),
    Slidess(
        "Notifications", "see all notifications", R.drawable.ic_notification
    )
    , Slidess(
        "Settings", "Notification, Language", R.drawable.setting
    ), Slidess(
        "Customers Order", "See all Customers order", R.drawable.additemcloud
    ), Slidess(
        "Add Items", "Add items to database", R.drawable.additemcloud
    )
)

