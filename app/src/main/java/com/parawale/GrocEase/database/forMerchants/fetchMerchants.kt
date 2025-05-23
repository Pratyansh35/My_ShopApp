package com.parawale.GrocEase.database.forMerchants

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.parawale.GrocEase.DataClasses.Dishfordb
import com.parawale.GrocEase.DataClasses.Merchant
import kotlinx.coroutines.tasks.await

suspend fun getAllMerchants(): List<Merchant> {
    val database = Firebase.database.reference
    val merchantsRef = database.child("merchants")
    val merchantsList = mutableListOf<Merchant>()

    return try {
        val snapshot = merchantsRef.get().await()
        for (childSnapshot in snapshot.children) {
            try {
                val merchant = childSnapshot.getValue(Merchant::class.java)
                val merchantCode = childSnapshot.key // <-- key like "7007254934"

                if (merchant != null && merchantCode != null) {
                    // Create a new Merchant object with the key injected
                    val updatedMerchant = merchant.copy(merchantCode = merchantCode)
                    Log.d("FirebaseData", "Merchant: $updatedMerchant")
                    merchantsList.add(updatedMerchant)

                }
            } catch (e: Exception) {
                Log.e("FirebaseData", "Error parsing merchant: ${e.message}")
            }
        }
        merchantsList
    } catch (e: Exception) {
        Log.e("FirebaseData", "Error fetching merchants: ${e.message}")
        emptyList()
    }
}


suspend fun fetchMerchantItems(merchantKey: String): List<Dishfordb> {
    val database = Firebase.database.reference
    val itemsRef = database.child("merchants").child("Items")
    val dishesList = mutableListOf<Dishfordb>()

    return try {
        val snapshot = itemsRef.get().await() // Use coroutine-based Firebase API for suspension
        for (childSnapshot in snapshot.children) {
            try {
                val dish = childSnapshot.getValue(Dishfordb::class.java)
                if (dish != null) {
                    dishesList.add(dish) // Add parsed Dishfordb to the list
                }
            } catch (e: Exception) {
                Log.e("FirebaseData", "Error parsing dish for merchant $merchantKey: ${e.message}")
            }
        }
        dishesList // Return successfully parsed dishes
    } catch (e: Exception) {
        Log.e("FirebaseData", "Error fetching items for merchant $merchantKey: ${e.message}")
        emptyList() // Return empty list on error
    }
}

