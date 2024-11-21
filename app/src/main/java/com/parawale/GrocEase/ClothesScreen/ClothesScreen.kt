package com.parawale.GrocEase.ClothesScreen


import android.content.Context
import android.os.Vibrator
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.parawale.GrocEase.DataClasses.Dishfordb
import com.parawale.GrocEase.mainScreen.diffLayouts.CartButton
import com.parawale.GrocEase.mainScreen.diffLayouts.PriceSection

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClothesScreen(
    clothesItems: List<Dishfordb>,
    cartItems: SnapshotStateList<Dishfordb>,
    updateTotals: () -> Unit,
    navController: NavController,
    onItemClick: (Dishfordb) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(clothesItems.size) { index ->
            ClothesItemCard(
                dish = clothesItems[index],
                cartItems = cartItems,
                updateTotals = updateTotals,
                navController = navController,
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
fun ClothesItemCard(
    dish: Dishfordb,
    cartItems: SnapshotStateList<Dishfordb>,
    navController: NavController,
    updateTotals: () -> Unit,
    onItemClick: (Dishfordb) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onItemClick(dish) },
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Product Image
            AsyncImage(
                model = dish.imagesUrl.first(),
                contentDescription = "Product Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .aspectRatio(3f / 4f),
                contentScale = ContentScale.Crop
            )

            // Product Title
            Text(
                text = dish.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Price and Discount
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price Section
                PriceSection(dish)


                // Add to Cart Button
                CartButton(
                    dish = dish,
                    cartItems = cartItems,
                    updateTotals = updateTotals,
                    vibrator = LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                )
            }
        }
    }
}
