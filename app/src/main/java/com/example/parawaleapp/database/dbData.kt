package com.example.parawaleapp.database

import android.util.Log

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import com.example.parawaleapp.database.Dishfordb
data class Dishfordb(
    val name: String = "",
    val price: String = "",
    var count: Int = 0,
    val description: String = "",
    val category: String = "",
    val imageUrl: String
){
    constructor() : this("", "", 0, "", "", "")
}
// RealTime Database
val mfirebaseDatabase =
    FirebaseDatabase.getInstance("https://myparawale-app-default-rtdb.asia-southeast1.firebasedatabase.app/")
val datareference = mfirebaseDatabase.reference
suspend fun getdata(): List<Dishfordb>? {
    return try {
        val task = datareference.get().await()

        val dishesList = mutableListOf<Dishfordb>()

        task.children.forEach { childSnapshot ->
            try {
                // Use getValue with a specific type to avoid potential issues
                val dish = childSnapshot.getValue(Dishfordb::class.java)
                dish?.let {
                    dishesList.add(it)
                }
            } catch (e: Exception) {
                Log.e("FirebaseData", "Error converting data: ${e.message}")
            }
        }

        dishesList
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



