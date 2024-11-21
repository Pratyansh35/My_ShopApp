package com.parawale.GrocEase.MedicinesScreen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.parawale.GrocEase.DataClasses.Dishfordb
import com.parawale.GrocEase.mainScreen.diffLayouts.LinearLayoutItems
import kotlin.math.log

@Composable
fun MedicinesScreen(
    medicinesItems: List<Dishfordb>,
    cartItems: SnapshotStateList<Dishfordb>,
    updateTotals: () -> Unit,
    navController: NavController,
    onItemClick: (Dishfordb) -> Unit
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(medicinesItems.size) { index ->
            LinearLayoutItems(
                dish = medicinesItems[index],
                cartItems = cartItems,
                updateTotals = updateTotals,
                onItemClick = { },
                navController = navController
            )
        }
    }
}

