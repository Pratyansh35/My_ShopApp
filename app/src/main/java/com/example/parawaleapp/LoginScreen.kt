package com.example.parawaleapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parawaleapp.sign_in.SignInState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun TypewriterText(
    label: String,
    padding: Modifier = Modifier.padding(all = 0.dp),
    fontsize: Int = 24.sp.value.toInt()
) {
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
fun SignInScreen(
    state: SignInState, onSignInClick: () -> Unit
) {
    var username by remember {
        mutableStateOf(TextFieldValue(""))
    }
    var password by remember {
        mutableStateOf(TextFieldValue(""))
    }
    val context = LocalContext.current
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()

        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFC5C1B1)),
        contentAlignment = Alignment.Center
    ) {


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column(
                Modifier
                    .fillMaxHeight(0.8f)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                TypewriterText("Parawale!", Modifier.padding(start = 16.dp), 40.sp.value.toInt())

                Text(
                    text = "Pharenda",
                    fontSize = 30.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, bottom = 20.dp)
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                    },
                    label = { Text(text = "Username") },
                    modifier = Modifier
                        .padding(10.dp)
                        .navigationBarsPadding(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    label = { Text(text = "Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .padding(10.dp)
                        .navigationBarsPadding()

                )
                TextButton(onClick = { /*TODO*/ }, Modifier.align(Alignment.End)) {
                    Text(
                        text = "Forgot Password?",
                        color = Color(0xFF4D7467),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                }
                Button(
                    onClick = {
                        if (username.text.isNotEmpty() && password.text.isNotEmpty()) {
                            Toast.makeText(
                                context, "Welcome to Parawale Kirana Store!", Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Invalid credentials." + "Please try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }, colors = ButtonDefaults.buttonColors(
                        Color(0xFF4D7467)
                    ), modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "Login", color = Color(0xFFEDEFEE)
                    )
                }
                Row {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(0.20f)
                            .align(Alignment.CenterVertically)
                    )
                    Text(
                        text = "OR",
                        fontSize = 14.sp,
                        color = Color(0xFF4D7467),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(0.30f)
                            .align(Alignment.CenterVertically)
                    )
                }

                Row {
                    Text(
                        text = "Sign in with",
                        fontSize = 14.sp,
                        color = Color(0xFF4D7467),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .align(Alignment.CenterVertically)
                    )

                    IconButton(onClick = { onSignInClick() }) {
                        Image(
                            painter = painterResource(id = R.drawable.googleicon),
                            contentDescription = "menuicon",
                            modifier = Modifier.size(44.dp)
                        )
                    }


                }
            }
            Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Bottom) {
                Text(
                    text = "Don't have an account?",
                    fontSize = 14.sp,
                    color = Color(0xFF4D7467),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}





