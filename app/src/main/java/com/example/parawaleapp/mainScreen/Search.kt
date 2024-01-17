package com.example.parawaleapp.mainScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parawaleapp.database.Categories
import com.example.parawaleapp.database.Dish
import com.example.parawaleapp.database.Dishes


@Composable
fun Search() {
    var searches by remember { mutableStateOf(TextFieldValue("")) }
    var visible by remember { mutableStateOf(true) }

    val filteredDishes = remember { mutableStateOf(emptyList<Dish>()) }

    // Update the filteredDishes whenever the search query changes
    DisposableEffect(searches.text) {
        val filterText = searches.text.lowercase()
        val filtered = Dishes.filter { dish ->
            dish.name.lowercase().contains(filterText)
        }
        filteredDishes.value = filtered
        onDispose { }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),

        ) {
        if (visible) {
            Row {

                Button(
                    onClick = { visible = false},
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD4D8AB)),
                    shape = RoundedCornerShape(40),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Search Now",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                    )
                }
            }
            Divider(
                modifier = Modifier.padding(8.dp),
                color = Color.Gray,
                thickness = 1.dp
            )


            LazyColumn {
                items(Dishes) { Dish ->
                    MenuDish(Dish)
                }
            }
        }
        else {
            Column(modifier = Modifier.fillMaxHeight()) {


                TextField(
                    value = searches,
                    onValueChange = { searches = it },
                    placeholder = { Text("Search here") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(40))
                )
                LazyRow {
                    items(Categories) { category ->
                        MenuCategory(category)
                    }
                }

                SearchFilter(filteredDishes.value)
            }
        }
    }
}

@Composable
fun SearchFilter(filteredDishes: List<Dish>) {
    Column {
        Divider(
            modifier = Modifier.padding(8.dp),
            color = Color.Gray,
            thickness = 1.dp
        )
        LazyColumn {
            items(filteredDishes) { dish ->
                MenuDish(dish)
            }
        }
    }
}