package com.example.parawaleapp.database

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

//fun updateCategoriesInDatabase(context: Context) {
//    val database = FirebaseDatabase.getInstance().reference.child("Items")
//
//    database.get().addOnSuccessListener { dataSnapshot ->
//        for (itemSnapshot in dataSnapshot.children) {
//            val itemKey = itemSnapshot.key ?: continue
//            val itemData = itemSnapshot.value as? Map<String, Any> ?: continue
//
//            val category = itemData["category"] as? String
//            if (category != null) {
//                // Convert the single category string to a list
//                val categories = listOf(category)
//
//                // Remove the old category field
//                database.child(itemKey).child("category").removeValue()
//
//                // Add the new categories list field
//                database.child(itemKey).child("categories").setValue(categories)
//            }
//        }
//
//        Toast.makeText(context, "Categories updated successfully", Toast.LENGTH_SHORT).show()
//    }.addOnFailureListener {
//        Toast.makeText(context, "Failed to update categories: ${it.message}", Toast.LENGTH_SHORT).show()
//    }
//}

fun updateCategoriesInDatabase(context: Context) {
    val database = FirebaseDatabase.getInstance().reference.child("OnlineOrders")

    database.get().addOnSuccessListener { dataSnapshot ->
        for (userSnapshot in dataSnapshot.children) {
            val ordersSnapshot = userSnapshot.child("orders")
            for (orderSnapshot in ordersSnapshot.children) {
                val orderKey = orderSnapshot.key ?: continue
                val itemsSnapshot = orderSnapshot.child("items")

                for (itemSnapshot in itemsSnapshot.children) {
                    val itemData = itemSnapshot.value as? Map<String, Any> ?: continue
                    val category = itemData["category"] as? String
                    if (category != null) {
                        // Convert the single category string to a list
                        val categories = listOf(category)

                        // Remove the old category field
                        val itemRef = database.child(userSnapshot.key!!).child("orders").child(orderKey).child("items").child(itemSnapshot.key!!)
                        itemRef.child("category").removeValue()

                        // Add the new categories list field
                        itemRef.child("categories").setValue(categories)
                    }
                }
            }
        }

        Toast.makeText(context, "Categories updated successfully", Toast.LENGTH_SHORT).show()
    }.addOnFailureListener {
        Toast.makeText(context, "Failed to update categories: ${it.message}", Toast.LENGTH_SHORT).show()
    }
}
