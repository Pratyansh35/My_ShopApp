package com.example.parawaleapp.DataClasses

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Stable
@Parcelize
data class Dishfordb(
    val name: String = "",
    val price: String = "",
    var count: Int = 0,
    var weight: String,
    val description: String = "",
    val categories: List<String> = listOf(), // Updated to List<String>
    val imagesUrl: MutableList<String> = mutableListOf(""),
    val barcode: String,
    val mrp: String,
    val totalcount: String,
    val id: String = UUID.randomUUID().toString(),
    val rating: Float = 4f,
    val isVeg: Boolean = false,
    val mainCategory: String = "Not defined",
) : Parcelable {
    constructor() : this("", "", 0,"", "", listOf(), mutableListOf(""), "", "", "")

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(price)
        dest.writeInt(count)
        dest.writeString(weight)
        dest.writeString(description)
        dest.writeStringList(categories) // Updated to write list of strings
        dest.writeString(imagesUrl.toString())
        dest.writeString(barcode)
        dest.writeString(mrp)
        dest.writeString(totalcount)
        Log.e("category", "categories: $categories")
    }
}