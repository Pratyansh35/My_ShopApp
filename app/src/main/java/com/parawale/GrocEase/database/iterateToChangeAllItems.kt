package com.parawale.GrocEase.database

import android.content.Context
import android.util.Log
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
fun categorizeItems(context: Context) {
    val database = FirebaseDatabase.getInstance().reference
    val itemsRef = database.child("Items")
    val categorizedRef = database.child("items")

    itemsRef.get().addOnSuccessListener { dataSnapshot ->
        for (itemSnapshot in dataSnapshot.children) {
            val itemData = itemSnapshot.value as? Map<String, Any?> ?: continue
            val mainCategory = itemData["mainCategory"] as? String ?: "Uncategorized"
            val itemKey = itemSnapshot.key!!

            // Reference to the correct category in the "items" section
            val targetCategoryRef = when (mainCategory) {
                "Groceries" -> categorizedRef.child("Groceries")
                "Electronics" -> categorizedRef.child("Electronics")
                "Medicines" -> categorizedRef.child("Medicines")
                "Apparel" -> categorizedRef.child("Apparel")
                "Food" -> categorizedRef.child("Food")
                else -> categorizedRef.child("Uncategorized") // Default category for uncategorized items
            }

            // Copy the item to the target category
            targetCategoryRef.child(itemKey).setValue(itemData).addOnSuccessListener {
                Log.d("Categorize", "Item $itemKey moved to $mainCategory")
            }.addOnFailureListener {
                Log.e("Categorize", "Failed to move item $itemKey: ${it.message}")
            }
        }

        Toast.makeText(context, "Items categorized successfully", Toast.LENGTH_SHORT).show()
    }.addOnFailureListener {
        Toast.makeText(context, "Failed to categorize items: ${it.message}", Toast.LENGTH_SHORT).show()
    }
}



fun updateItemsInDatabaseT(context: Context) {
    val database = FirebaseDatabase.getInstance().reference.child("Items")

    database.get().addOnSuccessListener { dataSnapshot ->
        for (itemSnapshot in dataSnapshot.children) {
            val itemRef = database.child(itemSnapshot.key!!)
            val itemData = itemSnapshot.value as? Map<String, Any?> ?: continue

            // Check and update 'isVeg' field
            val isVegValue = itemData["isVeg"]
            if (isVegValue == null) {
                // If 'isVeg' is missing, set default value "Undefined"
                itemRef.child("isVeg").setValue("Undefined")
            } else if (isVegValue is Boolean) {
                // If 'isVeg' is a Boolean, convert it to String "Veg" or "Non-Veg"
                val isVegString = if (isVegValue) "Veg" else "Non-Veg"
                itemRef.child("isVeg").setValue(isVegString)
            }

            // Check and update 'mainCategory' field
            val mainCategory = itemData["mainCategory"] as? String
            if (mainCategory == null) {
                // Set default value "Groceries" if 'mainCategory' is missing
                itemRef.child("mainCategory").setValue("Groceries")
            }

            // Check and update 'rating' field
            val rating = itemData["rating"]
            if (rating == null) {
                // Set default value 4.0 if 'rating' is missing
                itemRef.child("rating").setValue(4.0)
            }
        }

        Toast.makeText(context, "Items updated successfully", Toast.LENGTH_SHORT).show()
    }.addOnFailureListener {
        Toast.makeText(context, "Failed to update items: ${it.message}", Toast.LENGTH_SHORT).show()
    }
}
