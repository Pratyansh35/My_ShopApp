package com.example.parawaleapp.mainScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parawaleapp.database.cartItems
import com.example.parawaleapp.database.total


@Preview(showBackground = true)
@Composable
fun ConfirmCart() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text ="Parawale", modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),textAlign = TextAlign.Center,
            fontSize = 45.sp,
            color = androidx.compose.ui.graphics.Color.Red,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            , fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive)
        Text(text = "Cart Summary", modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold)
        CartLayout()
        LazyColumn(modifier = Modifier.weight(0.8f)){
            items(cartItems) { Dish ->
                ConfirmItems(Dish)
            }
        }
        Text(text = "Total Amount: ₹$total", modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold)
        androidx.compose.material.Button(
            onClick = {
            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF4CE14)),
            shape = RoundedCornerShape(40), modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.End)
        ) {
            androidx.compose.material.Text(
                text = "Print Bill",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CartLayout() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,

        ){
        Text(text = "Item Name", modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,fontWeight = FontWeight.Bold )
        Text(text = "Quantity", modifier = Modifier.weight(1f),textAlign = TextAlign.Center,fontWeight = FontWeight.Bold)
        Text(text = "MRP", modifier = Modifier.weight(1f),textAlign = TextAlign.Center,fontWeight = FontWeight.Bold)
        Text(text = "Total", modifier = Modifier.weight(1f),textAlign = TextAlign.Center,fontWeight = FontWeight.Bold)
    }
}



@Composable
fun ConfirmItems(dish: Dishfordb){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,

        ){
        Text(text = dish.name.take(21), modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center )
        Text(text = dish.count.toString(), modifier = Modifier.weight(1f),textAlign = TextAlign.Center)
        Text(text = dish.price, modifier = Modifier.weight(1f),textAlign = TextAlign.Center)
        Text(text = (dish.count * dish.price.removePrefix("₹").toDouble()).toString(), modifier = Modifier.weight(1f),textAlign = TextAlign.Center)
    }
}




