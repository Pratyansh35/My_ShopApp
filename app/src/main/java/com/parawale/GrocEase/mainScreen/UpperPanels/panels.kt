package com.parawale.GrocEase.mainScreen.UpperPanels

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parawale.GrocEase.R
import com.parawale.GrocEase.mainScreen.CategoryBox

@Composable
fun UserLocation(){

}

@Composable
fun UpperPanel() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.title),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
        )
        Text(
            text = stringResource(id = R.string.location),
            fontSize = 18.sp,
            color = MaterialTheme.colors.onSurface
        )
        Row(
            modifier = Modifier.padding(top = 18.dp),
            verticalAlignment = CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.description),
                color = MaterialTheme.colors.onSurface,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(bottom = 28.dp)
                    .fillMaxWidth(0.6f)
            )
            Image(
                painter = painterResource(id = R.drawable.parawale1),
                contentDescription = "Upper Panel Image",
                modifier = Modifier.clip(RoundedCornerShape(20.dp))
            )
        }
        Button(
            onClick = {
                Toast.makeText(context, "Select Interested Menu", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
        ) {
            Text(
                text = stringResource(id = R.string.orderbuttontext),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onPrimary
            )
        }
    }
}

@Composable
fun UpperPanel2() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()

            .padding(16.dp)
    ) {
        Text(
            text = "क्यों ना बढ़े हमभी",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.onSecondary),
            color = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Get Your Own App",
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Medium,
            color = Color(0xFF00BFA5)
        )

        Spacer(modifier = Modifier.height(16.dp))
        BulletPoint(text = "Make your business online")
        BulletPoint(text = "Easy delivery with Map live tracking")
        BulletPoint(text = "Barcode system")
        Row {
            Column {
                BulletPoint(text = "Easy billing via Bluetooth")
                BulletPoint(text = "All backend data handling")
            }
            Column(
                modifier = Modifier.fillMaxWidth()

            ) {
                Button(
                    onClick = {
                        // open dialer and dial number 7007254934
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:+917007254934")
                        context.startActivity(intent)
                    }, shape = RoundedCornerShape(20.dp), modifier = Modifier.align(Alignment.End),

                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                ) {
                    Text(
                        text = "Contact",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
        BulletPoint(text = "and Many more features")
    }
}

@Composable
fun UpperPanel3(onCategorySelected: (String) -> Unit) {
    val categories = listOf(
        Pair("Groceries", R.drawable.groceries_icon),
        Pair("Electronics", R.drawable.electronics_icon),
        Pair("Medicines", R.drawable.medicines_icon),
        Pair("Apparels", R.drawable.apparels_icon)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()

            .padding(16.dp)
    ) {
        Text(
            text = "Categories",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        val firstRow = categories.subList(0, 2)
        val secondRow = categories.subList(2, categories.size)

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
            ) {
                firstRow.forEach { category ->
                    CategoryBox(categoryName = category.first,
                        iconResId = category.second,
                        onClick = { onCategorySelected(category.first) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
            ) {
                secondRow.forEach { category ->
                    CategoryBox(categoryName = category.first,
                        iconResId = category.second,
                        onClick = { onCategorySelected(category.first) })
                }
            }
        }
    }
}


@Composable
fun BulletPoint(text: String) {
    Row(
        verticalAlignment = CenterVertically, modifier = Modifier.padding(bottom = 2.dp)
    ) {
        Text(
            text = "\u2022", // Unicode for bullet point
            fontSize = 14.sp, color = MaterialTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colors.onSurface
        )
    }
}