package com.example.parawaleapp.sign_in

//import com.example.parawaleapp.database.saveDataToSharedPreferences
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.parawaleapp.R
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: (Activity, String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onVerifyCodeClick: (String) -> Unit = {},
) {
    var phoneNumber by remember { mutableStateOf("") }
    val otp = remember { mutableStateListOf("", "", "", "", "", "") }
    val context = LocalContext.current as Activity
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val isFocusOnTextField = remember { mutableStateOf(false) }
    // Display sign-in errors as a Toast message
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            Log.e("SignInScreen", "Sign-in error: $it")
        }
    }

    // Show a loading dialog while sign-in is in progress
    if (state.isLoading) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9)),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.verificationId == null -> {
                // First screen: Phone Number input and Google Sign-In
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    TypewriterText(
                        "Parawale!",
                        Modifier.padding(if (isFocusOnTextField.value) WindowInsets.ime.asPaddingValues().calculateBottomPadding() + 16.dp else 16.dp),
                        40.sp.value.toInt()
                    )
                    Text(
                        text = "Sign up",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(bottom = if (isFocusOnTextField.value) WindowInsets.ime.asPaddingValues().calculateBottomPadding() + 4.dp else 4.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "keep ordering amazing",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(bottom = if (isFocusOnTextField.value) WindowInsets.ime.asPaddingValues().calculateBottomPadding() + 20.dp else 20.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    PhoneNumberInput(phoneNumber = phoneNumber,
                        onPhoneNumberChange = { phoneNumber = it },
                        onClearClick = { phoneNumber = "" },
                        onfocusChange = { isFocusOnTextField.value = it })


                    Button(
                        onClick = {
                            if (phoneNumber.isNotEmpty()) {
                                onSignInClick(context, "+91${phoneNumber}")
                                phoneNumber = ""
                            } else {
                                Toast.makeText(
                                    context, "Please enter a valid phone number.", Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFFE53935)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(if (isFocusOnTextField.value) WindowInsets.ime.asPaddingValues().calculateBottomPadding() + 16.dp else 16.dp)
                    ) {
                        Text(text = "Send OTP", color = Color.White)
                    }

                    // Divider and "OR" Text
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Divider(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color.Gray)
                        )
                        Text(
                            text = "OR",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Divider(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color.Gray)
                        )
                    }

                    // Google Sign-In Button
                    Button(
                        onClick = { onGoogleSignInClick() },
                        colors = ButtonDefaults.buttonColors(Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.googleicon),
                            contentDescription = "Google Icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Sign in with Google",
                            color = Color.Black,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                Text(
                    text = "By continuing, you agree to our Terms of Service and Privacy Policy",
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.BottomCenter),
                    textAlign = TextAlign.Center,
                )
            }

            else -> {
                // Second screen: OTP Verification with 6 boxes
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Verify your Phone number",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "+91${phoneNumber}",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        otp.forEachIndexed { index, value ->
                            OutlinedTextField(
                                value = value,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 1) {
                                        otp[index] = newValue
                                        if (newValue.isNotEmpty() && index < 5) {
                                            focusRequesters[index + 1].requestFocus()
                                        }
                                    }
                                },
                                label = null,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .size(50.dp)
                                    .focusRequester(focusRequesters[index])
                                    .onKeyEvent { keyevent ->
                                        if (keyevent.nativeKeyEvent.keyCode == Key.Backspace.nativeKeyCode) {
                                            if (otp[index].isEmpty() && index > 0) {
                                                focusRequesters[index - 1].requestFocus()
                                            }
                                        }
                                        false
                                    },
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val code = otp.joinToString("")
                            if (code.length == 6) {
                                onVerifyCodeClick(code)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter the complete verification code.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFFE53935)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(text = "Verify", color = Color.White)
                    }

                    // Resend OTP Option
                    TextButton(onClick = { /* TODO: Implement resend logic */ }) {
                        Text(
                            text = "Didn't receive any code? RESEND NEW CODE",
                            color = Color(0xFFE53935)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PhoneNumberInput(
    phoneNumber: String, onPhoneNumberChange: (String) -> Unit, onClearClick: () -> Unit,
    onfocusChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xFFF0F4F8), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        // Prefix "+91"
        Text(
            text = "+91 ", style = MaterialTheme.typography.body1, color = Color.Black
        )

        // Phone number input field
        OutlinedTextField(value = phoneNumber,
            onValueChange = { onPhoneNumberChange(it.take(10)) }, // Limit to 10 digits
            modifier = Modifier.weight(1f).onFocusChanged { onfocusChange(it.isFocused) },
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Black,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            placeholder = { Text(text = "Enter phone number", color = Color.Gray) },
            )

        // Cancel button to clear input
        if (phoneNumber.isNotEmpty()) {
            IconButton(onClick = { onClearClick() }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = Color.Gray
                )
            }
        }
    }
}

//@Composable
//fun SignInScreen(
//    state: SignInState,
//    onSignInClick: (Activity, String) -> Unit,
//    onGoogleSignInClick: () -> Unit,
//    onVerifyCodeClick: (String) -> Unit = {},
//) {
//    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
//    var verificationCode by remember { mutableStateOf(TextFieldValue("")) }
//    val context = LocalContext.current as Activity
//
//    // Display sign-in errors as a Toast message
//    LaunchedEffect(key1 = state.signInError) {
//        state.signInError?.let {
//            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
//            Log.e("SignInScreen", "Sign-in error: $it")
//        }
//    }
//
//    // Show a loading dialog while sign-in is in progress
//    if (state.isLoading) {
//        Dialog(onDismissRequest = {}) {
//            Box(
//                modifier = Modifier
//                    .size(100.dp)
//                    .background(Color.White, shape = RoundedCornerShape(8.dp)),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        }
//    }
//
//    // Main UI
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color(0xFFC5C1B1)),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center,
//            modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight()
//        ) {
//            Column(
//                Modifier
//                    .fillMaxHeight(0.8f)
//                    .padding(bottom = 16.dp),
//                verticalArrangement = Arrangement.Bottom,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                // Title or Branding Text
//                TypewriterText("Parawale!", Modifier.padding(start = 16.dp), 40.sp.value.toInt())
//                Text(
//                    text = "Pharenda",
//                    fontSize = 30.sp,
//                    color = Color.Black,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(start = 16.dp, bottom = 20.dp)
//                )
//
//                // Show any error message in a toast
//                if (state.errorMessage.isNotEmpty()) {
//                    Toast.makeText(LocalContext.current, state.errorMessage, Toast.LENGTH_SHORT).show()
//                }
//
//                // Verification Code Input
//                if (state.verificationId != null) {
//                    OutlinedTextField(
//                        value = verificationCode,
//                        onValueChange = {
//                            verificationCode = it
//                        },
//                        label = { Text(text = "Verification Code") },
//                        modifier = Modifier
//                            .padding(10.dp)
//                            .navigationBarsPadding(),
//                        singleLine = true
//                    )
//                    Button(
//                        onClick = {
//                            if (verificationCode.text.isNotEmpty()) {
//                                onVerifyCodeClick(verificationCode.text)
//                            } else {
//                                Toast.makeText(
//                                    context,
//                                    "Please enter the verification code.",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            }
//                        },
//                        colors = ButtonDefaults.buttonColors(Color(0xFF4D7467)),
//                        modifier = Modifier.padding(10.dp)
//                    ) {
//                        Text(text = "Verify", color = Color(0xFFEDEFEE))
//                    }
//                }
//
//                // Phone Number Input and Send OTP Button
//                if (!state.isSignInSuccessful) {
//                    OutlinedTextField(
//                        value = phoneNumber,
//                        onValueChange = {
//                            phoneNumber = it
//                        },
//                        label = { Text(text = "Phone Number") },
//                        modifier = Modifier
//                            .padding(10.dp)
//                            .navigationBarsPadding(),
//                        singleLine = true,
//                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
//                    )
//                    if (state.verificationId == null) {
//                        Button(
//                            onClick = {
//                                if (phoneNumber.text.isNotEmpty()) {
//                                    onSignInClick(context, "+91${phoneNumber.text}")
//                                } else {
//                                    Toast.makeText(
//                                        context,
//                                        "Please enter a valid phone number.",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//                                }
//                            },
//                            colors = ButtonDefaults.buttonColors(Color(0xFF4D7467)),
//                            modifier = Modifier.padding(10.dp)
//                        ) {
//                            Text(text = "Send OTP", color = Color(0xFFEDEFEE))
//                        }
//                    }
//                }
//
//                // Divider and "OR" Text
//                Row {
//                    Divider(
//                        modifier = Modifier
//                            .fillMaxWidth(0.20f)
//                            .align(Alignment.CenterVertically)
//                    )
//                    Text(
//                        text = "OR",
//                        fontSize = 14.sp,
//                        color = Color(0xFF4D7467),
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier
//                            .padding(start = 16.dp, end = 16.dp)
//                            .align(Alignment.CenterVertically)
//                    )
//                    Divider(
//                        modifier = Modifier
//                            .fillMaxWidth(0.30f)
//                            .align(Alignment.CenterVertically)
//                    )
//                }
//
//                // Google Sign-In Button
//                Row {
//                    Text(
//                        text = "Sign in with",
//                        fontSize = 14.sp,
//                        color = Color(0xFF4D7467),
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier
//                            .padding(start = 16.dp, end = 16.dp)
//                            .align(Alignment.CenterVertically)
//                    )
//
//                    IconButton(onClick = { onGoogleSignInClick() }) {
//                        Image(
//                            painter = painterResource(id = R.drawable.googleicon),
//                            contentDescription = "Google Icon",
//                            modifier = Modifier.size(44.dp)
//                        )
//                    }
//                }
//            }
//
//            // Footer Text for Sign-Up
//            Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Bottom) {
//                Text(
//                    text = "Don't have an account?",
//                    fontSize = 14.sp,
//                    color = Color(0xFF4D7467),
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier
//                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
//                        .align(Alignment.CenterHorizontally)
//                )
//            }
//        }
//    }
//}

@Composable
fun PhoneNumberLinkingDialog(
    state: SignInState,
    onDismissRequest: () -> Unit,
    onSendVerificationCodeClick: (String) -> Unit,
    onVerifyCodeClick: (String) -> Unit
) {
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var otpCode by rememberSaveable { mutableStateOf("") }
    var isOtpSent by rememberSaveable { mutableStateOf(false) }

    AlertDialog(onDismissRequest = {
        // Prevent dialog dismissal on back press or outside click
    }, title = { Text(text = "Link Phone Number") }, text = {
        Column {
            if (!isOtpSent) {
                TextField(value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text(text = "Phone Number") },
                    placeholder = { Text(text = "10 digit No.") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
                )
            } else {
                TextField(value = otpCode,
                    onValueChange = { otpCode = it },
                    label = { Text(text = "Enter OTP") },
                    placeholder = { Text(text = "OTP") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
            }
            if (state.errorMessage.isNotEmpty()) {
                Text(text = state.errorMessage, color = Color.Red)
            }
        }
    }, confirmButton = {
        if (isOtpSent) {
            Button(onClick = { onVerifyCodeClick(otpCode) }) {
                Text("Verify OTP")
            }
        } else {
            Button(onClick = {
                onSendVerificationCodeClick("+91$phoneNumber")
                isOtpSent = true
            }) {
                Text("Send Verification Code")
            }
        }
    }, dismissButton = {
        Button(onClick = {
            // Reset states and dismiss the dialog
            phoneNumber = ""
            otpCode = ""
            isOtpSent = false
            onDismissRequest()
        }) {
            Text("Cancel")
        }
    })
}


@Composable
fun TypewriterText(
    label: String, modifierPadding: Modifier = Modifier, fontsize: Int = 24.sp.value.toInt()
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
        modifier = modifierPadding
    )
}