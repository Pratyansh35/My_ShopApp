package com.example.parawaleapp.Ai

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.rememberNavController
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.mainScreen.DiffLayouts.LinearLayoutItems

@Composable
fun ItemSelectionPopup(
    dishData: List<Dishfordb>,
    onItemSelected: (Dishfordb) -> Unit,

    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp), elevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Select an item to add to cart", style = MaterialTheme.typography.h6
                )
                LazyColumn {
                    items(dishData){ dish ->
                        LinearLayoutItems(
                            dish = dish,
                            cartItems = remember { mutableStateListOf() }, // Provide appropriate cartItems
                            updateTotals = { /* Do nothing */ }, // Handle totals update here if needed
                            navController = rememberNavController() // Provide a NavController if needed
                        )
                    Log.d("dishData",dish.toString())
                    }

                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    }
}


//@Composable
//fun ItemSelectionPopup(
//
//)