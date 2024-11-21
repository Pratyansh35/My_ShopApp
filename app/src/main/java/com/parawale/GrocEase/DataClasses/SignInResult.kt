package com.parawale.GrocEase.DataClasses

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?,
    var isGoogleSignIn: Boolean = false
)


data class UserAddressDetails(
    val LocId: String = "",
    val name: String = "",
    val phone: String = "",
    val pincode: String = "",
    val address: String = "",
    val landmark: String = "",
    val city: String = "",
    val state: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val isHome: Boolean = true,
    val isWork: Boolean = false,
    val isDefault: Boolean = false
)



data class UserData(
    val userId: String,
    val userName: String?,
    val userEmail: String?,
    val profilePictureUrl: String?,
    val userPhoneNumber: String?,
    val isAdmin: Boolean,
    val isVerified: Boolean = false,
    val isMerchant: Boolean = false,
    var userAddressDetails: List<UserAddressDetails>? = null,
    var defaultAddress: UserAddressDetails? = null
)




