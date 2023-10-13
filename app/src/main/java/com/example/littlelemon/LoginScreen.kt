package com.example.littlelemon

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
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController

@Composable
fun LoginScreen() {
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
        Text(
            text = "Parawale!",
            fontSize = 60.sp,
            color = Color.Red,
            fontFamily = FontFamily.Cursive,
            modifier = Modifier.padding(start = 16.dp)
        )

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
            modifier = Modifier.padding(10.dp)
        )
        TextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(10.dp),
        )
        Button(
            onClick = {
                Log.d("AAA", "${username.text}")
                Log.d("AAA", "${password.text}")
                if (username.text == "parawale"
                    && password.text == "12345678"
                ) {

                    Toast.makeText(
                        context,
                        "Welcome to Parawale Kirana Store!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Invalid credentials."
                                + "Please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                Color(0xFF4D7467)
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







//@Composable
//fun mainScreen() {
//    val context = LocalContext.current
//    Column(
//        modifier = Modifier
//            .background(Color(0xFFC5C1B1))
//            .padding(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 16.dp)
//
//    ) {
//        Text(
//            text = "Parawale Kirana Store",
//            fontSize = 40.sp,
//            color = Color.Red,
//            fontFamily = FontFamily.Cursive,
//            fontWeight = FontWeight.Bold
//        )
//        Text(
//            text = "Pharenda, Maharajganj",
//            fontSize = 18.sp,
//            color = Color.Black,
//        )
//        Row(
//            modifier = Modifier
//                .padding(top = 18.dp)
//        )
//        {
//            Text(
//                text = stringResource(id = R.string.description),
//                color = Color.Black,
//                fontSize = 18.sp,
//                modifier = Modifier
//                    .padding(bottom = 28.dp)
//                    .fillMaxWidth(0.5f)
//            )
//            Image(
//                painter = painterResource(id = R.drawable.parawale1),
//                contentDescription = "Upper Panel Image",
//                modifier = Modifier
//
//                    .clip(RoundedCornerShape(20.dp)))
//
//        }
//        Button(onClick = {
//            Toast.makeText(context, "Order Succesful", Toast.LENGTH_SHORT).show() },
//            shape = RoundedCornerShape(10.dp),
//            colors = ButtonDefaults.buttonColors(
//                backgroundColor = Color(0xFFEC976F))
//        ) {
//            Text(text = "Order ")
//        }
//    }
//}