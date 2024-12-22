package com.parawale.GrocEase.DataClasses

data class Merchant(
    val name: String = "",
    val category: String = "",
    val contact: String = "",
    val logo: String = "",
    val address: MerchantAddress = MerchantAddress(),
    val items: Map<String, Dishfordb> = emptyMap()
)


data class MerchantAddress(
    val location: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val livelocation: LiveLocation = LiveLocation()
)


data class LiveLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

