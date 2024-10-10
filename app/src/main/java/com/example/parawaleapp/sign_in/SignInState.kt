package com.example.parawaleapp.sign_in

import com.example.parawaleapp.DataClasses.UserData

data class SignInState(
    val isLoading: Boolean = false,
    val signInError: String? = null,
    val isSignInSuccessful: Boolean = false,
    val verificationId: String? = null,
    val isGoogleSignIn: Boolean = false,
    val userData: UserData? = null,
    val errorMessage: String = ""
)

