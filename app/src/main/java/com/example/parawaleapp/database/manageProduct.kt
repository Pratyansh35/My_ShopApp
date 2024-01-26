package com.example.parawaleapp.database

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.w3c.dom.Text

@Preview(showBackground = true)
@Composable
fun manageItem(){
    Row(Modifier.height(54.dp)) {
        TextButton(onClick = { /*TODO*/ },
            Modifier.weight(1f).fillMaxHeight().align(Alignment.CenterVertically)
                .background(brush = Brush.horizontalGradient(colors = listOf(Color(0xFFA8A16F), Color(0xFFD37B6E)))
                ) ) {
            Text(
                text = "Add",
                color = Color(0xFFB3172F),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
        TextButton(onClick = { /*TODO*/ }, Modifier.weight(1f).align(Alignment.CenterVertically)) {
            Text(
                text = "Modify",
                color = Color(0xFF4D7467),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        TextButton(onClick = { /*TODO*/ }, Modifier.weight(1f).align(Alignment.CenterVertically)) {
            Text(
                text = "Delete",
                color = Color(0xFF4D7467),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}