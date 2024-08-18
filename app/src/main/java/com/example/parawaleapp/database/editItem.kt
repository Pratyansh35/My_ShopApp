package com.example.parawaleapp.database

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
@Composable
fun ModifyScreen(dish: Dishfordb, showModifyScreen: () -> Unit) {
    var name by remember { mutableStateOf(TextFieldValue(dish.name)) }
    val originalName by remember { mutableStateOf(dish.name) }
    var description by remember { mutableStateOf(TextFieldValue(dish.description)) }
    var price by remember { mutableStateOf(TextFieldValue(dish.price.trimStart('₹'))) }
    var weight by remember { mutableStateOf(TextFieldValue(dish.weight)) }
    Log.d("category", dish.categories.joinToString(", "))
    var category by remember { mutableStateOf(TextFieldValue(dish.categories.joinToString(", "))) }
    var imageUris by remember { mutableStateOf(dish.imagesUrl.toMutableList()) }
    var itembarcode by remember { mutableStateOf(dish.barcode) }
    var Itemmrp by remember { mutableStateOf(dish.mrp.trimStart('₹')) }
    var totalCount by remember { mutableStateOf(dish.totalcount) }
    var uploadProgress by remember { mutableStateOf(0f) }
    val context = LocalContext.current
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        imageUris = uris.map { it.toString() }.toMutableList()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Selected Images", modifier = Modifier.padding(start = 10.dp, top = 10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(10.dp)
        ) {
            imageUris.forEach { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(120.dp)
                        .clip(RoundedCornerShape(30))
                        .clickable {
                            multiplePhotoPickerLauncher.launch("image/*")
                        }
                )
            }
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(30))
                    .clickable {
                        multiplePhotoPickerLauncher.launch("image/*")
                    }
            )
        }

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
                keyboardType = KeyboardType.Text
            )
        )
        Row(
            modifier = Modifier
                .padding(8.dp)
                .height(88.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(value = Itemmrp,
                onValueChange = { Itemmrp = it },
                label = { Text(text = "Product MRP ₹") },
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f),
                maxLines = 1,
                textStyle = TextStyle(fontSize = 14.sp),
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

        OutlinedTextField(
            value = totalCount,
            onValueChange = { totalCount = it },
            label = { Text(text = "Total Availability") },
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp)
                .align(Alignment.CenterHorizontally),
            maxLines = 1,
            textStyle = TextStyle(fontSize = 14.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )
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
                if (name.text.isNotEmpty() && description.text.isNotEmpty() && price.text.isNotEmpty() && category.text.isNotEmpty() && imageUris.isNotEmpty()) {
                    val categoryList = category.text.split(",").map { it.trim() }  // Convert back to list
                    modifyItemOnDatabase(
                        name.text,
                        '₹' + price.text,
                        description.text,
                        weight.text,
                        originalName,
                        categoryList,
                        imageUris.map { Uri.parse(it) },
                        context,
                        itembarcode,
                        "₹$Itemmrp",
                        totalCount,
                        uploadProgress = { progress -> uploadProgress = progress }
                    )

                } else {
                    Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier
                .padding(top = 30.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Save")
        }

        if (uploadProgress > 0f) {
            LinearProgressIndicator(
                progress = uploadProgress,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            )
        }
    }
}


private fun modifyItemOnDatabase(
    name: String, price: String, description: String, weight: String, originalName: String,
    category: List<String>, images: List<Uri>, context: Context, itembarcode: String, itemmrp: String, totalCount: String,
    uploadProgress: (Float) -> Unit
) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val itemRef = datareference.child("Items").child(name)

    val scope = CoroutineScope(Dispatchers.Main)
    if (name != originalName) {
        datareference.child("Items").child(originalName).get().addOnSuccessListener {
            itemRef.setValue(it.value).addOnSuccessListener {
                datareference.child("Items").child(originalName).removeValue()
            }
        }
    }
    scope.launch(Dispatchers.IO) {
        val imageUrls = mutableListOf<String>()
        val newImageUris = images.filter { !it.toString().contains("firebasestorage.googleapis.com") }
        val existingImageUrls = images.filter { it.toString().contains("firebasestorage.googleapis.com") }

        // Delete old images from Firebase storage if necessary
        val oldImageUrls = itemRef.child("imagesUrl").get().await().children.map { it.value.toString() }
        oldImageUrls.forEach { imageUrl ->
            if (!existingImageUrls.contains(Uri.parse(imageUrl))) {
                val oldImageRef = storage.getReferenceFromUrl(imageUrl)
                oldImageRef.delete().await()
            }
        }

        val deferredResults = newImageUris.mapIndexed { index, uri ->
            async {
                val fileName = "$name$index"
                val imageRef = storageRef.child("Images/$fileName")
                val uploadTask = imageRef.putFile(uri)

                uploadTask.addOnProgressListener { snapshot ->
                    val progress = snapshot.bytesTransferred / snapshot.totalByteCount.toFloat()
                    uploadProgress(progress)
                }.await()

                imageRef.downloadUrl.await().toString()
            }
        }

        try {
            imageUrls.addAll(existingImageUrls.map { it.toString() })
            imageUrls.addAll(deferredResults.awaitAll())

            withContext(Dispatchers.Main) {
                if (imageUrls.size == images.size) {
                    itemRef.removeValue().await()
                    val item = mapOf(
                        "name" to name,
                        "price" to price,
                        "description" to description,
                        "weight" to weight,
                        "categories" to category,  // Now accepts a list of strings
                        "imagesUrl" to imageUrls,
                        "barcode" to itembarcode,
                        "mrp" to itemmrp,
                        "totalcount" to totalCount
                    )

                    itemRef.setValue(item).await()

                    Toast.makeText(context, "Item modified successfully", Toast.LENGTH_SHORT).show()
                    uploadProgress(0f)  // Reset progress bar
                } else {
                    Toast.makeText(context, "Error uploading images", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error uploading images: ${e.message}", Toast.LENGTH_SHORT).show()
                uploadProgress(0f)  // Reset progress bar
            }
        }
    }
}

