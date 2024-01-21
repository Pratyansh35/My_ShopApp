package com.example.parawaleapp.mainScreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import com.example.parawaleapp.database.Dishfordb
import java.net.URLDecoder

@Composable
fun LocationScreen() {
    Text(text ="Location Will be added soon", textAlign = TextAlign.Center)
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



