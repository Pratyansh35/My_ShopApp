package com.example.parawaleapp.database

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun ModifyScreen(dish: Dishfordb, showModifyScreen: () -> Unit) {
    var name by remember { mutableStateOf(TextFieldValue(dish.name)) }
    var description by remember { mutableStateOf(TextFieldValue(dish.description)) }
    var price by remember { mutableStateOf(TextFieldValue(dish.price)) }
    var category by remember { mutableStateOf(TextFieldValue(dish.category)) }
    var selectImgUri by remember { mutableStateOf(dish.imageUrl) } // Use selectImgUri instead of image
    val context = LocalContext.current
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
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

        OutlinedTextField(value = price,
            onValueChange = { price = it },
            label = { Text(text = "Product Price ₹") },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )


        Button(
            onClick = {
                if (name.text.isNotEmpty() && description.text.isNotEmpty() && price.text.isNotEmpty() && category.text.isNotEmpty() && selectImgUri != null) {
                    // Check if selectImgUri is a URL or a local URI
                    if (selectImgUri!!.startsWith("http") || selectImgUri!!.startsWith("https")) {
                        // If selectImgUri is a URL, create the updated Dishfordb object with the URL directly
                        val updatedDish = Dishfordb(
                            name = name.text,
                            description = description.text,
                            price = price.text,
                            category = category.text,
                            imageUrl = selectImgUri!!
                        )
                        // Update the database with the updated Dishfordb object
                        datareference.child(name.text).setValue(updatedDish).addOnSuccessListener {
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
                        selectImgUri?.let { imageUri ->
                            getImgUrl(Uri.parse(imageUri), context, name.text) { imgUrl ->
                                val updatedDish = imgUrl?.let { imageUrl ->
                                    Dishfordb(
                                        name = name.text,
                                        description = description.text,
                                        price = price.text,
                                        category = category.text,
                                        imageUrl = imageUrl
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
                        } ?: run {
                            Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT)
                                .show()
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




