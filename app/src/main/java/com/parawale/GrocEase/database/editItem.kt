package com.parawale.GrocEase.database

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.parawale.GrocEase.DataClasses.Dishfordb
import com.parawale.GrocEase.R
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

    var mainCategory by remember { mutableStateOf(dish.mainCategory.toString()) }
    var previousCategory by remember { mutableStateOf(dish.mainCategory.toString()) }
    var imageUris by remember { mutableStateOf(dish.imagesUrl.toMutableList()) }
    var itembarcode by remember { mutableStateOf(dish.barcode) }
    var Itemmrp by remember { mutableStateOf(dish.mrp.trimStart('₹')) }
    var totalCount by remember { mutableStateOf(dish.totalcount) }
    var uploadProgress by remember { mutableStateOf(0f) }
    val context = LocalContext.current

    var categoriesInput by remember { mutableStateOf(TextFieldValue(dish.categories.joinToString(", "))) }

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        imageUris = uris.map { it.toString() }.toMutableList()
    }

    var vegOrNot by remember { mutableStateOf(dish.isVeg) }
    val mainCategoryIcons: List<Pair<String, Int>> = listOf(
        Pair("Groceries", R.drawable.groceries_icon),
        Pair("Electronics", R.drawable.electronics_icon),
        Pair("Medicines", R.drawable.medicines_icon),
        Pair("Apparel", R.drawable.apparels_icon),
        Pair("Food", R.drawable.food_icon),
    )
    val categoriesVeg: List<Pair<String, Int>> = listOf(
        Pair("Veg", R.drawable.veg_icon),
        Pair("Non-Veg", R.drawable.nonveg_icon)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Display selected images
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
            // Add image button
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

        // Product name field
        OutlinedTextField(value = name,
            onValueChange = { name = it },
            label = { Text(text = "Product Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )
        Spacer(modifier = Modifier.height(8.dp))



            // Category Selection Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                mainCategoryIcons.forEach { category ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                mainCategory = category.first
                            }
                    ) {
                        val isSelected = mainCategory == category.first
                        Icon(
                            painter = painterResource(id = category.second),
                            contentDescription = "${category.first} Icon",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colors.primary else Color.Transparent
                                )
                                .border(
                                    4.dp,
                                    if (isSelected) Color.Green else Color.Transparent,
                                    CircleShape
                                ),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = category.first, style = TextStyle(fontSize = 12.sp))
                    }
                }
            }
            if (mainCategory == "Groceries" || mainCategory == "Medicines" || mainCategory == "Food") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    categoriesVeg.forEach { category ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    vegOrNot = category.first
                                }
                        ) {
                            val isSelected = vegOrNot == category.first
                            Icon(
                                painter = painterResource(id = category.second),
                                contentDescription = "${category.first} Icon",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colors.primary else Color.Transparent
                                    )
                                    .border(
                                        4.dp,
                                        if (isSelected) Color.Green else Color.Transparent,
                                        CircleShape
                                    ),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = category.first, style = TextStyle(fontSize = 12.sp))
                        }
                    }
                }
            }
        // Product description field
        OutlinedTextField(value = description,
            onValueChange = { description = it },
            label = { Text(text = "Product Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            maxLines = 4,
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Product category field
        OutlinedTextField(value = categoriesInput,
            onValueChange = { categoriesInput = it },
            label = { Text(text = "Product Category") },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(10.dp),
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Product weight field
        OutlinedTextField(value = weight,
            onValueChange = { weight = it },
            label = { Text(text = "Product Weight") },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(10.dp),
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )

        // Price and MRP fields
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

            // Display discount percentage if price is less than MRP
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

        // Total availability field
        OutlinedTextField(
            value = totalCount,
            onValueChange = { totalCount = it },
            label = { Text(text = "Total Availability") },
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterHorizontally),
            maxLines = 1,
            textStyle = TextStyle(fontSize = 14.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )

        // Barcode field
        OutlinedTextField(value = itembarcode,
            onValueChange = { itembarcode = it },
            label = { Text(text = "Product Barcode") },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(10.dp),
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Save button
        Button(
            onClick = {
                if (name.text.isNotEmpty() && description.text.isNotEmpty() && price.text.isNotEmpty() && categoriesInput.text.isNotEmpty() && imageUris.isNotEmpty()) {
                    val categoryList = categoriesInput.text.split(",").map { it.trim() }  // Convert back to list
                    modifyItemOnDatabase(
                        name = name.text,
                        price = '₹' + price.text,
                        description = description.text,
                        weight = weight.text,
                        originalName = originalName,
                        categories = categoryList,
                        images = imageUris.map { Uri.parse(it) },
                        itembarcode = itembarcode,
                        itemmrp = "₹$Itemmrp",
                        totalCount = totalCount,
                        isVeg = vegOrNot == "Veg",
                        mainCategory = mainCategory,
                        previousCategory = previousCategory,
                        uploadProgress = { progress -> uploadProgress = progress },
                        onSuccess = {
                            Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = {
                            Toast.makeText(context, "Failed Try again Later", Toast.LENGTH_SHORT).show()
                        }
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

        // Display upload progress
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
    name: String,
    price: String,
    description: String,
    weight: String,
    originalName: String,
    mainCategory: String,
    previousCategory: String,
    categories: List<String>,
    images: List<Uri>,
    itembarcode: String,
    itemmrp: String,
    totalCount: String,
    isVeg: Boolean,
    uploadProgress: (Float) -> Unit,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val itemRef = datareference.child("items").child(mainCategory).child(name)

    val scope = CoroutineScope(Dispatchers.Main)

    scope.launch(Dispatchers.IO) {
        try {
            // Handle renaming the item if the name has changed
            if (name != originalName) {
                val originalItemRef = datareference.child("items").child(previousCategory).child(originalName)
                val originalItemSnapshot = originalItemRef.get().await()

                if (originalItemSnapshot.exists()) {
                    itemRef.setValue(originalItemSnapshot.value).await()
                    originalItemRef.removeValue().await()
                } else {
                    withContext(Dispatchers.Main) {
                        onFailure("Original item does not exist.")
                    }
                    return@launch
                }
            }

            // Separate new and existing images
            val newImageUris = images.filter { !it.toString().contains("firebasestorage.googleapis.com") }
            val existingImageUrls = images.filter { it.toString().contains("firebasestorage.googleapis.com") }

            // Retrieve current image URLs from the database
            val currentImageUrls = itemRef.child("imagesUrl").get().await()
                .children.mapNotNull { it.value as? String }

            // Delete old images that are no longer needed
            val imagesToDelete = currentImageUrls - existingImageUrls.map { it.toString() }
            imagesToDelete.forEach { imageUrl ->
                val oldImageRef = storage.getReferenceFromUrl(imageUrl)
                oldImageRef.delete().await()
            }

            // Upload new images
            val deferredResults = newImageUris.mapIndexed { index, uri ->
                async {
                    val fileName = "$name-${System.currentTimeMillis()}-$index"
                    val imageRef = storageRef.child("Images/$fileName")
                    val uploadTask = imageRef.putFile(uri)

                    uploadTask.addOnProgressListener { snapshot ->
                        val progress = (snapshot.bytesTransferred / snapshot.totalByteCount.toFloat())
                        uploadProgress(progress)
                    }.await()

                    imageRef.downloadUrl.await().toString()
                }
            }

            // Combine existing and new image URLs
            val updatedImageUrls = existingImageUrls.map { it.toString() } + deferredResults.awaitAll()

            // Update the item in the database
            val updatedItem = mapOf(
                "name" to name,
                "price" to "$price",
                "description" to description,
                "weight" to weight,
                "categories" to categories,
                "imagesUrl" to updatedImageUrls,
                "barcode" to itembarcode,
                "mrp" to "$itemmrp",
                "totalcount" to totalCount,
                "rating" to 4f,
                "isVeg" to isVeg,
                "mainCategory" to mainCategory
            )

            itemRef.setValue(updatedItem).await()

            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onFailure(e.message ?: "An unknown error occurred.")
            }
        } finally {
            withContext(Dispatchers.Main) {
                uploadProgress(0f) // Reset progress bar
            }
        }
    }
}


