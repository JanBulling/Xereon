package com.xereon.xereon.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class IPLocationResponse(
    val city : String,
    val country : String,
    @SerializedName("lat")
    val latitude : Float,
    @SerializedName("lng")
    val longitude : Float,
    val zip : String
) {
    val location get() = country + " " + city + " (" + zip + ")"
}