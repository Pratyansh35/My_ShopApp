package com.example.parawaleapp.sign_in

import com.google.firebase.auth.PhoneAuthProvider

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?,
    var isGoogleSignIn: Boolean = false
)


data class UserData(
    val userId: String,
    val userName: String?,
    val userEmail: String?,
    val profilePictureUrl: String?,
    val phoneno: String?
)

data class SignInState(
    val isLoading: Boolean = false,
    val signInError: String? = null,
    val isSignInSuccessful: Boolean = false,
    val verificationId: String? = null,
    val resendToken: PhoneAuthProvider.ForceResendingToken? = null,
    val isPhoneNumberLinked: Boolean = false,
    val isGoogleSignIn: Boolean = false,
    val userData: UserData? = null, // Add this property
    val errorMessage: String = ""
)


