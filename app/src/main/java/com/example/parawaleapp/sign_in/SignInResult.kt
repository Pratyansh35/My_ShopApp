package com.example.parawaleapp.sign_in

data class SignInResult(
    val data: UserData?, val errorMessage: String?
)

data class UserData(
    val userId: String,
    val userName: String?,
    val userEmail: String?,
    var progilePictureUrl: String?
)