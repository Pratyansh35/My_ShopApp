package com.example.parawaleapp.barcodeScreen


import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.mainScreen.truncateString

@Composable
fun ItemScreen(
    dish: Dishfordb, showItemScreen: () -> Unit,
    cartItems: MutableList<Dishfordb>,
    updateTotals: () -> Unit,
    //saveCartItemsToSharedPreferences: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = Uri.parse(dish.imagesUrl[0]),
            contentDescription = "dishImage",
            modifier = Modifier
                .padding(start = 10.dp)
                .size(120.dp)
                .clip(RoundedCornerShape(30))
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = truncateString(dish.name, 25),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = truncateString(dish.description, 65),
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Category: ${dish.category}",
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Price: ${dish.price}",
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Barcode: ${dish.barcode}",
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (!cartItems.contains(dish)) {
                    cartItems.add(dish)
                }
                dish.count++
                updateTotals()
                //saveCartItemsToSharedPreferences()
                Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show()
                showItemScreen()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF4CE14)),
            shape = RoundedCornerShape(40),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 30.dp)
        ) {
            Text(text = "Add to Cart")
        }
    }
}

