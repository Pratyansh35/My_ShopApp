package com.parawale.GrocEase.ViewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.parawale.GrocEase.DataClasses.Dishfordb


class SharedViewModel: ViewModel() {
    val selectedItems = mutableStateOf<List<Dishfordb>>(emptyList())

    val orderedItems = mutableStateOf<List<Dishfordb>>(emptyList())
    fun setItems(items: List<Dishfordb>) {
        selectedItems.value = items
    }

    fun setOrderedItems(items: List<Dishfordb> ) {
        orderedItems.value = items
    }
    fun getOrderedItems(): List<Dishfordb> {
        return orderedItems.value
    }
}