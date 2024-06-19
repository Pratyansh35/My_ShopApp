package com.example.parawaleapp.drawerPanel.leftPanel

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.parawaleapp.database.img
import com.example.parawaleapp.database.name
import com.example.parawaleapp.database.phoneno
import com.example.parawaleapp.database.saveDataToSharedPreferences
import com.example.parawaleapp.sign_in.UserData


@Composable
fun Profileset(userData: UserData?) {

//    var img = ImageBitmap.imageResource(R.drawable.mypic4)
    // var img = Uri.parse("android.resource://com.example.parawaleapp/drawable/mypic4")
    val context = LocalContext.current

    var nametemp by remember {
        mutableStateOf(userData?.userName)
    }
    var phonenotemp by remember {
        mutableStateOf(phoneno)
    }
    var selectImgUri by remember {
        mutableStateOf(
            userData?.progilePictureUrl
        )
    }

    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                if (uri != null) {
                    selectImgUri = uri.toString()
                }
            })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(10.dp),
        Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // AndroidView(modifier = Modifier.fillMaxSize(), factory = { videoView })
        Text(
            text = "Parawale",
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 45.sp,
            color = Color.Red,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive
        )

        Text(
            text = "Settings",
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        nametemp?.let {
            OutlinedTextField(value = it,
                onValueChange = { nametemp = it },
                label = { Text(text = "Full name") },
                modifier = Modifier.padding(10.dp),
                singleLine = true
            )
        }

        OutlinedTextField(value = phonenotemp,
            onValueChange = {
                phonenotemp = it
                phoneno = it
            },
            label = { Text(text = "Phone no.") },
            modifier = Modifier.padding(10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )

        Row(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                text = "Profile Picture",
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.CenterVertically),
                textAlign = TextAlign.Center,

                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            AsyncImage(model = if (img.toString().equals("")) {
                userData?.progilePictureUrl
            } else img!!,
                contentDescription = "userImage",
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(80.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    })
        }
        androidx.compose.material.Button(
            onClick = {
                if (nametemp == "" || phonenotemp == "") {
                    Toast.makeText(
                        context, "Please fill all the fields", Toast.LENGTH_LONG
                    ).show()
                    return@Button
                } else if (phonenotemp.length != 10) {
                    Toast.makeText(
                        context, "Please enter a valid phone no.", Toast.LENGTH_LONG
                    ).show()
                    return@Button
                } else {
                    name = nametemp.toString()
                    phoneno = phonenotemp
                    img = Uri.parse(selectImgUri.toString())
                    saveDataToSharedPreferences(context)
                    Toast.makeText(
                        context, "Profile Updated", Toast.LENGTH_LONG
                    ).show()
                }
            },
            colors = androidx.compose.material.ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFF4CE14)
            ),
            shape = RoundedCornerShape(40),
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.End)
        ) {
            Text(
                text = "Save", color = Color.Black, fontWeight = FontWeight.Bold
            )
        }
    }
}