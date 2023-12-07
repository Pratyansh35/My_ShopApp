package com.example.parawaleapp

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Preview(showBackground = true)
@Composable
fun Pre(){
    var isUserLoggedIn by remember { mutableStateOf(false) }
    LoginScreen(onLoginSuccess = {
        // This block is executed when the login is successful.
        isUserLoggedIn = true
    })
}



@Composable
fun TypewriterText(label: String, padding: Modifier = Modifier.padding(all = 0.dp),
fontsize: Int = 24.sp.value.toInt()) {
    var displayedText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(label) {
        coroutineScope.launch {
            for (letter in label) {
                displayedText += letter.toString()
                delay(200)
            }
        }
        onDispose {
            coroutineScope.cancel()
        }
    }

    Text(
        text = displayedText,
        fontSize = fontsize.sp,
        color = Color.Red,
        fontFamily = FontFamily.Cursive,
        modifier = padding
    )
}



@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {



    val context = LocalContext.current
    var username by remember {
        mutableStateOf(TextFieldValue(""))
    }
    var password by remember {
        mutableStateOf(TextFieldValue(""))
    }


    Column(
        modifier = Modifier
            .background(Color(0xFFC5C1B1))
            .padding(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 16.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {


            TypewriterText("Parawale!", Modifier.padding(start = 16.dp), 40.sp.value.toInt())

            Text(
                text = "Pharenda",
                fontSize = 30.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, bottom = 20.dp)
            )
            TextField(
                value = username,
                onValueChange = {
                    username = it
                },
                label = { Text(text = "Username") },
                modifier = Modifier.padding(10.dp),
                singleLine = true
            )
            TextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true

            )
            TextButton(onClick = { /*TODO*/ }, Modifier.align(Alignment.End) ) {
                Text(text = "Forgot Password?", color = Color(0xFF4D7467), fontWeight = FontWeight.Bold,fontSize = 14.sp)

            }
            Button(
                onClick = {
                    Log.d("AAA", username.text)
                    Log.d("AAA", password.text)
                    if (username.text.isNotEmpty() && password.text.isNotEmpty()
                    ) {
                        Toast.makeText(
                            context,
                            "Welcome to Parawale Kirana Store!",
                            Toast.LENGTH_LONG
                        ).show()
                        onLoginSuccess()
                    } else {
                        Toast.makeText(
                            context,
                            "Invalid credentials."
                                    + "Please try again.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                          },
                colors = ButtonDefaults.buttonColors( Color(0xFF4D7467)
                ),
                modifier = Modifier.padding(10.dp)
            )
            {
                Text(
                    text = "Login",
                    color = Color(0xFFEDEFEE)
                )
            }
        }
    }
}






