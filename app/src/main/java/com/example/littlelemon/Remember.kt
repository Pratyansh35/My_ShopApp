package com.example.littlelemon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlelemon.ui.theme.ui.theme.LittleLemonTheme


@Composable
fun Greeting() {
    var count by rememberSaveable {
        mutableStateOf(0)
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Card() {
            Column(modifier = Modifier.padding(20.dp)) {
                Row() {
                    Text(
                        text = "Total Items",
                        fontWeight = FontWeight.W700,
                        fontSize = 30.sp
                    )
                    Text(
                        text = "$count",
                        fontWeight = FontWeight.W700,
                        fontSize = 30.sp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { count -= 1 }) {
                        Text(text = "-")
                    }
                    TextButton(onClick = { count += 1 }) {
                        Text(text = "+")
                    }
                }
            }
            Button(onClick = { /*TODO*/ }, Modifier.fillMaxWidth()) {
                Text(text = "ADD")
            }

        }
    }

}



@Composable
fun GreetingPreview() {
    LittleLemonTheme {

    }
}