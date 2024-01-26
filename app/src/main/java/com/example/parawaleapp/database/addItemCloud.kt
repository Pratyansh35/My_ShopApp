package com.example.parawaleapp.database

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.parawaleapp.R
import com.example.parawaleapp.sign_in.UserData

@Composable
fun AddItemScreen(userData: UserData?) {
    val context = LocalContext.current
    var name by remember {
        mutableStateOf("")
    }
    var price by remember {
        mutableStateOf("")
    }
    var description by remember {
        mutableStateOf("")
    }
    var category by remember {
        mutableStateOf("")
    }
    var image by remember {
        mutableStateOf<Uri?>(null)
    }
    var imgSize by remember {
        mutableIntStateOf(0)
    }

    storageReference.listAll().addOnSuccessListener { result ->
        val imageCount = result.items.size
        imgSize = imageCount;
    }.addOnFailureListener { exception ->
        // Handle failure
        Toast.makeText(context, " error ${exception.message}", Toast.LENGTH_SHORT).show()
        println("Failed to list items in the folder: $exception")
    }
    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                if (uri != null) {
                    image = uri
                }
            })
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (userData?.userEmail != "pratyansh35@gmail.com"){
               Text(text ="You are not authorized to access this page",
                   style = TextStyle(
                       fontSize = 20.sp,
                       fontWeight = FontWeight.Bold
                   ), textAlign = TextAlign.Center)
        }else{
        Text(
            text = "Add Item Screen",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add ${imgSize + 1}th Item",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Product Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            maxLines = 2,
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
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

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text(text = "Product Category") },
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

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text(text = "Product Price ₹") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            maxLines = 1,
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

    Row(modifier = Modifier
        .padding(top = 16.dp)
        .height(100.dp),
        verticalAlignment = Alignment.CenterVertically,) {
        Text(
            text = "Product Image",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        AsyncImage(model = image ?: R.drawable.additemcloud,
            contentDescription = "productImage",
            modifier = Modifier
                .padding(start = 10.dp)
                .size(80.dp)
                .clickable {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
        )
    }

        Button(
            onClick = { if(name.isNullOrBlank() || price.isNullOrBlank() || description.isNullOrBlank() || image == null || imgSize == 0){
                            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                        }else{
                            addItemToDatabase( name,
                                "₹$price", description, category, image,context,imgSize)
            }
                      },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF4CE14)),
            shape = RoundedCornerShape(15),
            modifier = Modifier
                .padding(10.dp)

        ) {
            androidx.compose.material.Text(text = "Add to Database")
        }
    }
    }
}

fun addItemToDatabase(name: String, price: String, description: String, category: String, image: Uri?, context: Context, imgSize: Int) {
    getImgUrl(image, context, name) { imgUrl ->
        // Use the obtained imgUrl to create the Dishfordb object
        val dish = imgUrl?.let { Dishfordb(name, price, 0, description, category, it) }

        // Push the dish to the "dishes" node in the database
        val pushedReference = datareference.push()
        pushedReference.setValue(dish)
    }
}

fun getImgUrl(image: Uri?, context: Context, name: String, callback: (String?) -> Unit) {
    image?.let { uri ->
        val imageRef = storageReference.child(name)
        imageRef.putFile(uri)
            .addOnSuccessListener { _ ->
                // Image upload successful
                Toast.makeText(context, "Item Added Successfully", Toast.LENGTH_SHORT).show()

                // Retrieve the download URL
                imageRef.downloadUrl.addOnSuccessListener { imgUrl ->
                    callback(imgUrl.toString())
                }
                    .addOnFailureListener { exception ->
                        // Handle the error while obtaining the download URL
                        Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                        callback(null)
                    }
            }
            .addOnFailureListener { exception ->
                // Handle the error during image upload
                Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                Log.d("uploadData", "addItemToDatabase: ${exception.message}")
                callback(null)
            }
    } ?: callback(null) // Handle the case where image is null
}
