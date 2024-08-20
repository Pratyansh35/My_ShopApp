package com.example.parawaleapp.ViewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.parawaleapp.database.Dishfordb

class SharedViewModel: ViewModel() {
    val selectedItems = mutableStateOf<List<Dishfordb>>(emptyList())

    fun setItems(items: List<Dishfordb>) {
        selectedItems.value = items
    }
}