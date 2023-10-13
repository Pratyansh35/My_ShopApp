package com.example.littlelemon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
@Composable
fun MenuListScreen() {
    Column {
        UpperPanelmenu()
        LowerPanel()
    }

}

@Composable
private fun UpperPanelmenu() {
    Column(
        modifier = Modifier
            .background(Color(0xFF495E57))
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.title),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF4CE14)
        )

    }
    Text(
        text = "Order for Takeaway",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
private fun LowerPanel() {
    Column {
        LazyRow {
            items(Categories) { category ->
                MenuCategory(category)
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
}

@Composable
fun MenuCategory(category: String) {
    Button(
        onClick = { /*TODO*/ },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
        shape = RoundedCornerShape(40),
        modifier = Modifier.padding(5.dp)
    ) {
        Text(
            text = category
        )
    }
}

@Composable
fun MenuDish(Dish: Dish) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column {
                Text(
                    text = Dish.name, fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    text = Dish.description,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                        .fillMaxWidth(.75f)
                )
                Text(
                    text = Dish.price, color = Color.Gray, fontWeight = FontWeight.Bold
                )
            }
            Image(
                painter = painterResource(id = Dish.image),
                contentDescription = "",
            )
        }
    }
    Divider(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
        color = Color.LightGray,
        thickness = 1.dp
    )
}

val Categories = listOf(
    "DryFruits",
    "Pulses",
    "Grocery",
    "Main",
    "Spices"
)


data class Dish(
    val name: String,
    val price: String,
    val description: String,
    val image: Int
)

val Dishes = listOf(
    Dish(
        "Fortune Refined Oil(1ltr)",
        "₹110",
        "fulfills the body's needs for Omega 3 fatty acids Soy Oil...",
        R.drawable.fortune110
    ),
    Dish(
        "PatanJali Cow Ghee(500ml)",
        "₹355",
        "Patanjali Cow's Ghee is made from the milk of indigenous cows. It is...",
        R.drawable.patanjali355
    ),
    Dish(
        "Tata Salt(1kg)",
        "₹18",
        "Tata Salt is one of the most recognizable brands in India. Tata Salt...",
        R.drawable.tatasalt18
    ),
    Dish(
        "Daawat Rozana Gold(5kg)",
        "₹350",
        "Daawat Rozana Gold is the finest Basmati Rice in the mid-price...",
        R.drawable.daawatrozanasuper399
    ),
    Dish(
        "Tata Sampann Chana Dal(1kg)",
        "₹118.75",
        "Tata Sampann Unpolished Chana Dal is made from 100% unpolished...",
        R.drawable.tatachanadaal118
    ),
    Dish(
        "Mejestic- Cake Rusk | Extra Soft | 350g",
        "₹240",
        "Majestic Cake Rusk is a delicious and crunchy snack that is...",
        R.drawable.mejestirusk240
    ),
    Dish(
        "Happilo Almonds 500g",
        "₹339",
        "100% Natural Premium California Dried Almonds are a great source of protein...",
        R.drawable.happilo339
    ),
    Dish(
        "Nutraj Walnut Kernels,(2 X 250g)",
        "₹499",
        "Nutraj California Walnuts are a great source of protein, fibre...",
        R.drawable.nutralwallnut498
    ),
    Dish(
        "Nutraj Long Raisin 500g",
        "₹175",
        "| Kishmish |Super Rich in Iron & Vitamin B | Seedless Green Kishmish | Healthy Snacks | Dry Fruits",
        R.drawable.nutrajkismis175
    )
)