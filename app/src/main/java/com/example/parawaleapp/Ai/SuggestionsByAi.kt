package com.example.parawaleapp.Ai

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.parawaleapp.DataClasses.Dishfordb
import com.example.parawaleapp.mainScreen.diffLayouts.LinearLayoutItems

@Composable
fun ItemSelectionPopup(
    suggestions: List<Dishfordb>,
    cartItems: SnapshotStateList<Dishfordb>,
    updateTotals: () -> Unit,
    navController: NavController,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp), elevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Select an item to add to cart", style = MaterialTheme.typography.h6
                )
                LazyColumn(modifier = Modifier.padding(8.dp).height(560.dp).fillMaxWidth()) {
                    items(suggestions){ dish ->
                        LinearLayoutItems(
                            dish = dish,
                            cartItems = cartItems,
                            updateTotals,
                            navController = navController,
                            onItemClick = {}
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