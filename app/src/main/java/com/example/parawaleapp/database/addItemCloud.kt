package com.example.parawaleapp.database

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


suspend fun getRelatedItems(query: String): List<Dishfordb>? {
    return try {
        val task =
            datareference.child("Items").orderByChild("name").startAt(query).endAt(query + "\uf8ff")
                .get().await()
        Log.e("FirebaseData", "Task result: ${task.value}")
        val result = task.children.mapNotNull { childSnapshot ->
            try {
                val item = childSnapshot.getValue(Dishfordb::class.java)
                Log.e("FirebaseData", "Fetched item: ${item?.name}")
                item
            } catch (e: Exception) {
                Log.e("FirebaseData", "Error converting data: ${e.message}")
                null
            }
        }
        Log.e("FirebaseData", "Result size: ${result.size}")
        result
    } catch (e: Exception) {
        Log.e("FirebaseData", "Exception retrieving data: $e")
        null
    }
}

@Composable
fun AddItemScreen() {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var images by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var imgSize by remember { mutableIntStateOf(0) }
    var itembarcode by remember { mutableStateOf("") }
    var itemmrp by remember { mutableStateOf("") }
    var totalCount by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<Dishfordb>>(emptyList()) }
    val focusManager = LocalFocusManager.current
    var uploadProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(name) {
        if (name.isNotEmpty()) {
            suggestions = getRelatedItems(name) ?: emptyList()
        } else {
            suggestions = emptyList()
        }
        Log.e("suggestions", suggestions.toString())
    }

    val multiplePhotoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents(),
            onResult = { uris ->
                if (uris.isNotEmpty()) {
                    images = uris.take(4) // Restrict to a maximum of 4 images
                }
            })

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text(
                text = "Add Item Screen",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface
                ),
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Add ${imgSize + 1}th Item", style = TextStyle(
                    fontSize = 12.sp, fontWeight = FontWeight.Normal, color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = name,
                onValueChange = { name = it },
                label = { Text(text = "Product Name") },
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
                maxLines = 2,
                textStyle = TextStyle(fontSize = 12.sp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
        }

        items(suggestions) { suggestion ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background)
                    .height(50.dp)
                    .padding(4.dp), elevation = 4.dp
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        name = suggestion.name
                        price = suggestion.price.removePrefix("₹")
                        description = suggestion.description
                        category = suggestion.category
                        weight = suggestion.weight
                        itemmrp = suggestion.mrp.removePrefix("₹")
                        itembarcode = suggestion.barcode
                        images = listOf(Uri.parse(suggestion.imagesUrl[0]))
                        suggestions = emptyList()
                        focusManager.clearFocus()
                    }
                    .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = suggestion.name, modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AsyncImage(
                        model = suggestion.imagesUrl[0],
                        contentDescription = "productImage",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(value = description,
                onValueChange = { description = it },
                label = { Text(text = "Product Description") },
                modifier = Modifier.padding(10.dp),
                maxLines = 4,
                textStyle = TextStyle(fontSize = 12.sp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = category,
                onValueChange = { category = it },
                label = { Text(text = "Product Category") },
                modifier = Modifier.padding(10.dp),
                maxLines = 1,
                textStyle = TextStyle(fontSize = 12.sp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row {
                OutlinedTextField(value = weight,
                    onValueChange = { weight = it },
                    label = { Text(text = "Product weight") },
                    modifier = Modifier.padding(
                        start = 10.dp,
                        end = 10.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    ),
                    textStyle = TextStyle(fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    )
                )

            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .height(88.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(value = itemmrp,
                    onValueChange = { itemmrp = it },
                    label = { Text(text = "MRP ₹") },
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
                    label = { Text(text = "Sell Price ₹") },
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f),
                    maxLines = 1,
                    textStyle = TextStyle(fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )

                if (price.isNotEmpty() && itemmrp.isNotEmpty() && price.toFloat() < itemmrp.toFloat()) {
                    Text(
                        text = " -${"%.2f".format(((itemmrp.toFloat() - price.toFloat()) / itemmrp.toFloat()) * 100)}%",
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
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
                maxLines = 1,
                textStyle = TextStyle(fontSize = 12.sp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
            OutlinedTextField(value = totalCount,
                onValueChange = { totalCount = it },
                label = { Text(text = "Total Availability") },
                modifier = Modifier.padding(4.dp),
                maxLines = 1,
                textStyle = TextStyle(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Product Images", style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(10.dp)
            ) {
                images.forEach { uri ->
                    AsyncImage(model = uri,
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(120.dp)
                            .clip(RoundedCornerShape(30))
                            .clickable {
                                multiplePhotoPickerLauncher.launch("image/*")
                            })
                }
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = "Add Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(30))
                        .clickable {
                            multiplePhotoPickerLauncher.launch("image/*")
                        })
            }

            Button(
                onClick = {
                    if (name.isNullOrBlank() || price.isNullOrBlank() || description.isNullOrBlank() || images.isEmpty()) {
                        Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        if (itembarcode.isEmpty()) {
                            itembarcode = name
                        }
                        uploadImagesAndAddItemToDatabase(name,
                            "₹$price",
                            description,
                            weight,
                            category,
                            images,
                            context,
                            itembarcode,
                            itemmrp,
                            totalCount,
                            uploadProgress = { progress -> uploadProgress = progress })

                    }
                }, shape = RoundedCornerShape(15), modifier = Modifier
                    .padding(20.dp)
                    .height(40.dp)
            ) {
                Text(text = "Add to Database")
            }

            if (uploadProgress > 0f) {
                LinearProgressIndicator(
                    progress = uploadProgress, modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

private fun uploadImagesAndAddItemToDatabase(
    name: String,
    price: String,
    description: String,
    weight: String,
    category: String,
    images: List<Uri>,
    context: Context,
    itembarcode: String,
    itemmrp: String,
    totalCount: String,
    uploadProgress: (Float) -> Unit
) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference

    val scope = CoroutineScope(Dispatchers.Main)

    scope.launch(Dispatchers.IO) {
        val imageUrls = mutableListOf<String>()
        val deferredResults = images.mapIndexed { index, uri ->
            async {
                val fileName = "$name$index"
                val imageRef = storageRef.child("Images/$fileName")
                val uploadTask = imageRef.putFile(uri).await()

                imageRef.downloadUrl.await().toString()
            }
        }

        try {
            // Awaiting the results of all async tasks
            imageUrls.addAll(deferredResults.awaitAll())

            withContext(Dispatchers.Main) {
                if (imageUrls.size == images.size) {
                    val item = mapOf(
                        "name" to name,
                        "₹price" to price,
                        "description" to description,
                        "weight" to weight,
                        "category" to category,
                        "imagesUrl" to imageUrls,
                        "barcode" to itembarcode,
                        "₹mrp" to itemmrp,
                        "totalcount" to totalCount
                    )

                    datareference.child("Items").child(name).setValue(item)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Item added successfully", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Error adding item: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(context, "Error uploading images", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error uploading images: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}


//fun addItemToDatabase(
//    name: String,
//    price: String,
//    description: String,
//    weight: String,
//    category: String,
//    image: Uri?,
//    context: Context,
//    barcode: String,
//    mrp: String
//) {
//    getImgUrl(image, context, name) { imgUrl ->
//        val dish = imgUrl?.let {
//            Dishfordb(
//                name,
//                price,
//                0,
//                weight,
//                description,
//                category,
//                it,
//                barcode,
//                mrp
//            )
//        }
//        datareference.child("Items").child(name).setValue(dish).addOnSuccessListener {
//            Toast.makeText(context, "Item Added Successfully", Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener { exception ->
//            Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
//            Log.d("uploadData", "addItemToDatabase: ${exception.message}")
//        }
//    }
//}
//
//fun getImgUrl(image: Uri?, context: Context, name: String, callback: (String?) -> Unit) {
//    image?.let { uri ->
//        val imageRef = storageReference.child(name)
//        imageRef.putFile(uri).addOnSuccessListener { _ ->
//            Toast.makeText(context, "Item Added Successfully", Toast.LENGTH_SHORT).show()
//            imageRef.downloadUrl.addOnSuccessListener { imgUrl ->
//                callback(imgUrl.toString())
//            }.addOnFailureListener { exception ->
//                Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
//                callback(null)
//            }
//        }.addOnFailureListener { exception ->
//            Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
//            Log.d("uploadData", "addItemToDatabase: ${exception.message}")
//            callback(null)
//        }
//    } ?: callback(null)
//}
