package com.parawale.GrocEase.Location

import android.util.Log
import com.parawale.GrocEase.DataClasses.UserAddressDetails
import com.parawale.GrocEase.DataClasses.UserData
import com.parawale.GrocEase.database.datareference
import kotlinx.coroutines.tasks.await

fun uploadLocation(
    userData: UserData,
    userAddressDetails: List<UserAddressDetails>?
) {
    try {

        userAddressDetails?.forEach { address ->
            val locId = datareference.child("Locations").child(userData.userId).push().key
            locId?.let {
                // Upload the address under its LocId
                datareference.child("Locations").child(userData.userId).child(locId).setValue(address)
            }
        }
    } catch (e: Exception) {
        Log.e("Firebase", "Error uploading location: $e")
    }
}


fun deleteLocation(
    userData: UserData,
    userAddressDetails: UserAddressDetails
) {
    try {
        datareference.child("Locations").child(userData.userId).child(userAddressDetails.name).removeValue()
    } catch (e: Exception) {
        Log.e("Firebase", "Error deleting location: $e")
    }
}

suspend fun getLocations(userData: UserData): List<UserAddressDetails> {
    return try {
        val snapshot = datareference.child("Locations").child(userData.userId).get().await()

        if (snapshot.exists()) {
            snapshot.children.mapNotNull { childSnapshot ->
                try {
                    childSnapshot.getValue(UserAddressDetails::class.java)
                } catch (e: Exception) {
                    Log.e("FirebaseData", "Error converting data: ${e.message}")
                    null
                }
            }
        } else {
            Log.e("FirebaseData", "No data found for user: ${userData.userId}")
            emptyList() // No data found
        }
    } catch (e: Exception) {
        Log.e("FirebaseData", "Exception retrieving data: $e")
        emptyList() // Return an empty list on error
    }
}

