package com.example.parawaleapp


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Preview(showBackground = true)
@Composable
fun AfterConfirm(){

 Column(
     Modifier
         .fillMaxHeight()
         .fillMaxWidth()
         .padding(top = 50.dp),
     horizontalAlignment = Alignment.CenterHorizontally
 ) {
        Text(text = "Confirm Your Cart",
            fontSize = 24.sp,
            fontFamily = FontFamily.Monospace)

    /* Row(modifier = Modifier.fillMaxWidth()) {
         Text(text = "Dish Name",
             fontSize = 14.sp,
             fontFamily = FontFamily.Monospace)

         Text(text = "Price",
             fontSize = 14.sp,
             fontFamily = FontFamily.Monospace,
             modifier = Modifier.padding(start = 10.dp))
         Text(text = "total-Items",
             fontSize = 14.sp,
             fontFamily = FontFamily.Monospace,
             modifier = Modifier.padding(start = 10.dp))
         Text(text = "Final-Price",
             fontSize = 14.sp,
             fontFamily = FontFamily.Monospace,
             modifier = Modifier.padding(start = 10.dp))
     }*/
    LazyHorizontalStaggeredGrid(
        rows = StaggeredGridCells.Fixed(4),
    modifier = Modifier
        .fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalItemSpacing = 40.dp
    ){
        item() {
            CartHeaderRow()
        }

        items(cartItems) { Dish ->
            ListCart(Dish)
        }

        }
    }

}



@Composable
fun CartScreen(cartItems: List<Dish>,) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        CartHeaderRow()
        cartItems.forEach { dish ->
            ListCart(dish)
        }
    }
}

@Composable
fun CartHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CartHeaderText(text = "Dish Name")
        CartHeaderText(text = "Price")
        CartHeaderText(text = "Total Items")
        CartHeaderText(text = "Final Price")
    }
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = Color.Gray,
        thickness = 1.dp
    )
}

@Composable
fun CartHeaderText(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier
            .padding(horizontal = 4.dp)

    )
}

@Composable
fun ListCart(Dish: Dish) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CartItemText(text = if (Dish.name.length > 30) "${Dish.name.take(22)}..." else Dish.name)
        CartItemText(text = Dish.price)
        CartItemText(text = Dish.count.toString())
        CartItemText(
            text = "₹${Dish.count * Dish.price.removePrefix("₹").toDouble()}",
            modifier = Modifier.weight(0.5f)
        )
    }
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = Color.Gray,
        thickness = 0.5.dp
    )
}

@Composable
fun CartItemText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontFamily = FontFamily.Monospace,
        modifier = modifier
            .padding(horizontal = 4.dp)
          ,
        textAlign = TextAlign.Center
    )
}


/*@Composable
fun CartHeaderRow() {
    Row(modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceEvenly) {
        Text(
            text = "Dish Name",
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Price",
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Total Items",
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Final Price",
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@Composable
fun listCart(Dish: Dish , navController: NavController? = null){
    val displayedText = if (Dish.name.length > 30) {
        Dish.name.take(22) + "..."// Take up to 30 characters
    } else {
        Dish.name // Use the original name if it's 30 characters or fewer
    }
    Row(modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceEvenly) {
        Text(text = displayedText,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace)

        Text(text = Dish.price,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(start = 10.dp))

        Text(text = Dish.count.toString(),
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(start = 10.dp))

        Text(text = "₹${Dish.count * Dish.price.removePrefix("₹").toDouble()}",
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(0.2f),

        )

    }
    Divider(
            modifier = Modifier.padding(8.dp),
    color = Color.Gray,
    thickness = 0.5.dp
    )
}*/
