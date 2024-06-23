package com.example.parawaleapp.database

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

data class Dishfordb(
    val name: String = "",
    val price: String = "",
    var count: Int = 0,
    var weight: String,
    val description: String = "",
    val category: String = "",
    val imageUrl: String,
    val barcode: String,
    val mrp: String
) {
    constructor() : this("", "", 0,"", "", "", "", "", "")
}



// RealTime Database
val mfirebaseDatabase =
    FirebaseDatabase.getInstance("https://myparawale-app-default-rtdb.asia-southeast1.firebasedatabase.app/")
val datareference = mfirebaseDatabase.reference
suspend fun getdishes(child : String): List<Dishfordb>? {
    return try {
        val task = datareference.child(child).get().await()

        task.children.mapNotNull { childSnapshot ->
            try {
                childSnapshot.getValue(Dishfordb::class.java)
            } catch (e: Exception) {
                Log.e("FirebaseData", "Error converting data: ${e.message}")
                null
            }
        }
    } catch (e: Exception) {
        // Handle exceptions if any
        Log.e("FirebaseData", "Exception retrieving data: $e")
        null
    }
}


// Firebase Storage for Images
val storage = FirebaseStorage.getInstance().reference
val storageReference = storage.child("Images")


var ItemCount = 0;


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



