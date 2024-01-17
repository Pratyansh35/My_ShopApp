package com.example.parawaleapp.database

import android.util.Log
import androidx.compose.runtime.getValue
import com.example.parawaleapp.mainScreen.Dishfordb
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.math.log


// RealTime Database
val mfirebaseDatabase = FirebaseDatabase.getInstance("https://myparawale-app-default-rtdb.asia-southeast1.firebasedatabase.app/")
val datareference = mfirebaseDatabase.reference;
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
                Log.e("FirebaseData", "Error converting data: $e")
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

        imageUrlList
    } catch (exception: Exception) {
        // Handle failure to list items
        Log.e("FireBaseImage", "Exception retrieving data: $exception")
        emptyList()
    }
}



