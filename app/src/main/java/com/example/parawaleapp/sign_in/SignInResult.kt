package com.example.parawaleapp.sign_in

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
    val userPhoneNumber: String?
)




