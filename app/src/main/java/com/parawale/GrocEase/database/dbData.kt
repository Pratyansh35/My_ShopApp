package com.parawale.GrocEase.database

import android.util.Log
import com.parawale.GrocEase.DataClasses.Dishfordb
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await


// RealTime Database


val datareference = FirebaseDatabase.getInstance().reference

suspend fun getAllDishes(): List<Dishfordb> {
    val categories = listOf("Groceries", "Electronics", "Medicines", "Apparel", "Food")
    val allDishes = mutableListOf<Dishfordb>()

    return try {
        for (category in categories) {
            val task = datareference.child("items").child(category).get().await()

            // Collect dishes from the current category
            val dishes = task.children.mapNotNull { childSnapshot ->
                try {
                    childSnapshot.getValue(Dishfordb::class.java)

                } catch (e: Exception) {
                    Log.e("FirebaseData", "Error converting data for $category: ${e.message}")
                    null
                }
            }

            // Add the dishes from the current category to the overall list
            allDishes.addAll(dishes)
        }

        allDishes // Return the list with all the dishes
    } catch (e: Exception) {
        Log.e("FirebaseData", "Exception retrieving data: $e")
        emptyList()
    }
}



// Firebase Storage for Images
val storage = FirebaseStorage.getInstance().reference
val storageReference = storage.child("Images")


var ItemCount = 0


suspend fun getImages(): List<String> {
    return try {
        val result = storageReference.listAll().await()
        val imageUrlList = mutableListOf<String>()

        for (item in result.items) {
            val uri = item.downloadUrl.await()
            val imageUrl = uri.toString()
            Log.e("FireBaseImage", "Image URL: $imageUrl")
            imageUrlList.add(imageUrl)
        }
        ItemCount = imageUrlList.size
        imageUrlList
    } catch (exception: Exception) {
        // Handle failure to list items
        Log.e("FireBaseImage", "Exception retrieving data: $exception")
        emptyList()
    }
}



