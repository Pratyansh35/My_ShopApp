package com.parawale.GrocEase.mainScreen.diffLayouts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.parawale.GrocEase.DataClasses.Merchant
import com.parawale.GrocEase.R

@Composable
fun MerchantCard(
    merchant: Merchant,
    onViewItemsClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(180.dp)
            .clickable { onViewItemsClick(merchant.contact) },
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
        ) {
            // Logo Section
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = merchant.logo,
                    contentDescription = "Merchant Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
                // Discount Badge
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE53935)) // Red badge
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = "FLAT ₹100 OFF", // Example badge
                        style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            // Details Section
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = merchant.name,
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.Gray)
                    Text(
                        text = "${merchant.address.city}, ${merchant.address.state}",
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    //Icon(Icons.Default.AccessTime, contentDescription = "Delivery Time", tint = Color.Gray)
                    Image(
                        painter = painterResource(id = R.drawable.deliverytime),
                        contentDescription = "Delivery Time",
                        modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    )
                    Text(
                        text = "30 mins",
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray
                    )
                }
            }
            // Action Button
            Button(
                onClick = { onViewItemsClick(merchant.contact) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Text(text = "View Items")
            }
        }
    }
}

@Composable
fun MerchantsGrid(
    merchants: List<Merchant>,
    onViewItemsClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // 2 cards per row
        modifier = Modifier.padding(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(merchants.size) { merchant ->
            MerchantCard(
                merchant = merchants[merchant],
                onViewItemsClick = onViewItemsClick
            )
        }
    }
}
