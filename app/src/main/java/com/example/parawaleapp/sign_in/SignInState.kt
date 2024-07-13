package com.example.parawaleapp.sign_in

data class SignInState(
    val isLoading: Boolean = false,
    val signInError: String? = null,
    val verificationId: String? = null,
    val isSignInSuccessful: Boolean = false,
    val userData: UserData? = null,
    val isPhoneNumberVerificationVisible: Boolean = false,
    val isPhoneNumberLinked: Boolean = false // Add this field
)
