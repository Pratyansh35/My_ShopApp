package com.example.parawaleapp.database

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.database.FirebaseDatabase

@Composable
fun deleteItems() {

}

@Composable
fun deleteItemLayout(dish: Dishfordb) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column {
                Text(
                    text = dish.name, fontSize = 16.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    text = dish.description,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                        .fillMaxWidth(.75f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(modifier = Modifier.fillMaxWidth(.3f)) {
                        Text(
                            text = dish.price, color = Color.Gray, fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            AsyncImage(
                model = Uri.parse(dish.imagesUrl[0]),
                contentDescription = "dishImage",
            )
        }
    }
    Divider(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
        color = Color.LightGray,
        thickness = 1.dp
    )
}


fun deleteItem(imageUrl: String, itemName: String, context: Context, callback: (Boolean) -> Unit) {
    val imageRef = storageReference.child(itemName)

    // Delete the image from Firebase Storage
    imageRef.delete().addOnSuccessListener {
        // Image successfully deleted
        Toast.makeText(context, "Image Deleted Successfully", Toast.LENGTH_SHORT).show()

        // Now, delete the item from the Realtime Database
        val databaseRef = FirebaseDatabase.getInstance().getReference("items").child(itemName)
        databaseRef.removeValue().addOnSuccessListener {
            // Item successfully deleted from Realtime Database
            Toast.makeText(context, "Item Deleted Successfully", Toast.LENGTH_SHORT).show()
            callback(true)
        }.addOnFailureListener { exception ->
            // Handle the error during item deletion from Realtime Database
            Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
            Log.d("deleteItem", "deleteItemFromDatabase: ${exception.message}")
            callback(false)
        }
    }.addOnFailureListener { exception ->
        // Handle the error during image deletion from Firebase Storage
        Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
        Log.d("deleteItem", "deleteImage: ${exception.message}")
        callback(false)
    }
}
