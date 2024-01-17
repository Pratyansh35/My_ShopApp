package com.example.parawaleapp.mainScreen

import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.parawaleapp.database.cartItems
import com.example.parawaleapp.database.countItems
import com.example.parawaleapp.database.datareference
import com.example.parawaleapp.database.getImages
import com.example.parawaleapp.database.getdata
import com.example.parawaleapp.database.totalcount

import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun LocationScreen() {
    var datauser by remember { mutableStateOf<List<Dishfordb>>(emptyList()) }
    var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        // Fetch dish data
        getdata()?.let { newData ->
                datauser = newData
            }
        // Fetch image URLs
        imageUrls = getImages()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Display the dishes using the dbpanel composable
        LazyColumn {
            items(datauser) { dish ->
                // Display each dish using your composable
                MenuDishfromDb(dish, getImageUrlForDish(dish, imageUrls))
            }
        }
    }
}
private fun getImageUrlForDish(dish: Dishfordb, imageUrls: List<String>): String {
    // Get the name of the dish without decoding
    val dishName = dish.name.trim()

    // Find the matching image URL based on the dish name
    return imageUrls.find { imageUrl ->
        // Decode the image URL to compare with the trimmed dish name
        val decodedImageUrl = URLDecoder.decode(imageUrl, "UTF-8").trim()
        decodedImageUrl.contains(dishName, ignoreCase = true)
    } ?: ""
}
data class Dishfordb(
    val name: String = "",
    val price: String = "",
    var count: Int = 0,
    val description: String = "",
    val category: String = "",
)


data class WithImageDish(
    val name: String = "",
    val price: String = "",
    var count: Int = 0,
    val description: String = "",
    val category: String = "",
    val imageUrl: String
)



@Composable
fun MenuDishfromDb(dish: Dishfordb, imageUrl: String) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column {
                androidx.compose.material.Text(
                    text = dish.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                androidx.compose.material.Text(
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
                        androidx.compose.material.Text(
                            text = dish.price,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(.6f), Arrangement.End) {
                        Button(
                            onClick = {
                                /*if (cartItems.contains(dish)) {
                                    dish.count++
                                    countItems()
                                    totalcount()
                                } else {
                                    dish.count++
                                    cartItems.add(dish)
                                    totalcount()
                                    countItems()
                            }*/
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF4CE14)),
                            shape = RoundedCornerShape(40)
                        ) {
                            androidx.compose.material.Text(text = "Add to Cart")
                        }
                    }
                }
            }
            AsyncImage(
                model = imageUrl,
                contentDescription = "dishImage",
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(80.dp)
            )

        }
    }
    Divider(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
        color = Color.LightGray,
        thickness = 1.dp
    )
}
/*@Composable
fun dbpanel(datauser: List<Dishfordb>) {
    LazyColumn {
        items(datauser) { dish ->
            MenuDishfromDb(dish)
        }
    }
}*/






/*@Composable
fun LocationScreen() {
    var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        // Call your function to get image URLs
        imageUrls = getImages()
    }
    LaunchedEffect(Unit) {
        getdata()?.let { newData ->
            datauser = newData
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Display the images using the ImageList composable
        ImageList(imageUrls)
    }
}
@Composable
fun ImageList(images: List<String>) {
    LazyColumn {
        items(images) { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = "userImage",
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(80.dp)
            )
        }
    }
}*/
