package com.parawale.GrocEase.ElectronicScreen

import android.content.Context
import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.parawale.GrocEase.DataClasses.Dishfordb
import com.parawale.GrocEase.mainScreen.diffLayouts.CartButton
import com.parawale.GrocEase.mainScreen.diffLayouts.DiscountBadge
import com.parawale.GrocEase.mainScreen.diffLayouts.PriceSection
import com.parawale.GrocEase.mainScreen.diffLayouts.SlidableDishImage
import com.parawale.GrocEase.mainScreen.diffLayouts.calculateDiscount
import com.parawale.GrocEase.mainScreen.diffLayouts.navigateToItemDescription
import com.parawale.GrocEase.mainScreen.truncateString

@Composable
fun ElectronicHomePage(
    electronicItems: List<Dishfordb>,
    cartItems: SnapshotStateList<Dishfordb>,
    updateTotals: () -> Unit,
    navController: NavController,
    onItemClick: (Dishfordb) -> Unit
) {


    // Display the filtered electronic items in a scrollable column
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(electronicItems.size) { electronicItem ->
            ElectronicItemCard(
                dish = electronicItems[electronicItem],
                cartItems = cartItems,
                updateTotals = updateTotals,
                navController = navController,
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
fun ElectronicItemCard(
    dish: Dishfordb,
    cartItems: SnapshotStateList<Dishfordb>,
    navController: NavController,
    updateTotals: () -> Unit,

    onItemClick: (Dishfordb) -> Unit
) {
    val context = LocalContext.current
    val discountPercentage = calculateDiscount(dish.mrp, dish.price)
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            //.clickable { onItemClick(dish) }
        ,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colors.onSecondary)
                .padding(4.dp)
                .clickable(onClick = { navController.navigateToItemDescription(dish) }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier

                    .clip(RoundedCornerShape(20.dp))
                    .align(Alignment.CenterVertically)
                    .padding(end = 4.dp)
                    .clickable(onClick = {
                        onItemClick(dish)
                        navController.navigateToItemDescription(dish) })
            ) {
                SlidableDishImage(
                    imageUrls = dish.imagesUrl,
                    imageHeight = 185.dp,
                    imageWidth = 145.dp
                )
                DiscountBadge(discountPercentage)
            }

            Column(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Product name
                Text(
                    text = truncateString(dish.name, 30),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface,
                    maxLines = 1
                )

                // Product description
                Text(
                    text = truncateString(dish.description, 60),
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 2
                )

                // Pricing section with MRP and discounted price
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(top = 8.dp, end = 12.dp).fillMaxWidth()
                ) {
                    // Display the MRP and discounted price
                    PriceSection(dish)

                    // Add to Cart button
                    CartButton(
                        dish = dish,
                        cartItems = cartItems,
                        updateTotals = updateTotals,
                        vibrator = vibrator
                    )
                }
            }
        }
    }
}

