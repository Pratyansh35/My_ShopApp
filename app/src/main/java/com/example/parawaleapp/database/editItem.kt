package com.example.parawaleapp.database

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun ModifyScreen(dish: Dishfordb, showModifyScreen: () -> Unit) {
    var name by remember { mutableStateOf(TextFieldValue(dish.name)) }
    var description by remember { mutableStateOf(TextFieldValue(dish.description)) }
    var price by remember { mutableStateOf(TextFieldValue(dish.price.trimStart('₹'))) }
    var weight by remember { mutableStateOf(TextFieldValue(dish.weight)) }
    var category by remember { mutableStateOf(TextFieldValue(dish.category)) }
    var selectImgUri by remember { mutableStateOf(dish.imageUrl) }
    var itembarcode by remember { mutableStateOf(dish.barcode) }
    var Itemmrp by remember { mutableStateOf(dish.mrp.trimStart('₹')) }
    val context = LocalContext.current
    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                if (uri != null) {
                    selectImgUri = uri.toString()
                }
            })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(model = selectImgUri,
            contentDescription = "userImage",
            modifier = Modifier
                .padding(start = 10.dp)
                .size(120.dp)
                .clip(RoundedCornerShape(30))
                .align(Alignment.CenterHorizontally)
                .clickable {
                    singlePhotoPickerLauncher.launch("image/*")
                })

        OutlinedTextField(value = name,
            onValueChange = { name = it },
            label = { Text(text = "Product Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = description,
            onValueChange = { description = it },
            label = { Text(text = "Product Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
            maxLines = 4,
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = category,
            onValueChange = { category = it },
            label = { Text(text = "Product Category") },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = weight,
            onValueChange = { weight = it },
            label = { Text(text = "Product weight") },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )
        Row(
            modifier = Modifier
                .padding(8.dp) // Consistent padding around the Row
                .height(88.dp) // Fixed height for the Row
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Ensures even spacing between elements
        ) {
            OutlinedTextField(value = Itemmrp,
                onValueChange = { Itemmrp = it },
                label = { Text(text = "Product MRP ₹") },
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f), // Weight ensures equal space for each TextField
                maxLines = 1,
                textStyle = TextStyle(fontSize = 14.sp), // Slightly increased font size
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )

            OutlinedTextField(value = price,
                onValueChange = { price = it },
                label = { Text(text = "Product Price ₹") },
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f),
                maxLines = 1,
                textStyle = TextStyle(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )

            if (price.text.isNotEmpty() && Itemmrp.isNotEmpty() && price.text.toFloat() < Itemmrp.toFloat()) {
                Text(
                    text = " -${"%.2f".format(((Itemmrp.toFloat() - price.text.toFloat()) / Itemmrp.toFloat()) * 100)}%",
                    style = TextStyle(
                        fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray
                    ),
                    modifier = Modifier.padding(start = 4.dp, end = 1.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = itembarcode,
            onValueChange = { itembarcode = it },
            label = { Text(text = "Product barcode") },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (name.text.isNotEmpty() && description.text.isNotEmpty() && price.text.isNotEmpty() && category.text.isNotEmpty() && selectImgUri != null) {
                    // Check if selectImgUri is a URL or a local URI
                    if (selectImgUri.startsWith("http") || selectImgUri.startsWith("https")) {
                        // If selectImgUri is a URL, create the updated Dishfordb object with the URL directly
                        val updatedDish = Dishfordb(
                            name = name.text,
                            description = description.text,
                            weight = weight.text,
                            price = '₹' + price.text,
                            category = category.text,
                            imageUrl = selectImgUri,
                            barcode = itembarcode,
                            mrp = "₹$Itemmrp"
                        )
                        // Update the database with the updated Dishfordb object
                        datareference.child("Items").child(name.text).setValue(updatedDish)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context, "Item Updated Successfully", Toast.LENGTH_SHORT
                                ).show()
                                showModifyScreen()
                            }.addOnFailureListener { exception ->
                            Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                            Log.d("uploadData", "addItemToDatabase: ${exception.message}")
                        }
                    } else {
                        // If selectImgUri is a local URI, upload it first
                        selectImgUri.let { imageUri ->
                            getImgUrl(Uri.parse(imageUri), context, name.text) { imgUrl ->
                                val updatedDish = imgUrl?.let { imageUrl ->
                                    Dishfordb(
                                        name = name.text,
                                        description = description.text,
                                        weight = weight.text,
                                        price = '₹' + price.text,
                                        category = category.text,
                                        imageUrl = imageUrl,
                                        barcode = itembarcode,
                                        mrp = "₹$Itemmrp"
                                    )
                                }
                                updatedDish?.let { dish ->
                                    // Update the database with the updated Dishfordb object
                                    datareference.child(name.text).setValue(dish)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Item Updated Successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            showModifyScreen()
                                        }.addOnFailureListener { exception ->
                                            Toast.makeText(
                                                context, exception.message, Toast.LENGTH_SHORT
                                            ).show()
                                            Log.d(
                                                "uploadData",
                                                "addItemToDatabase: ${exception.message}"
                                            )
                                        }
                                } ?: run {
                                    Toast.makeText(
                                        context, "Failed to update item", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier
                .padding(top = 30.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Save")
        }
    }
}




