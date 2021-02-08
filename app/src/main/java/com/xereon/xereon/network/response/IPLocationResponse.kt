package com.xereon.xereon.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class IPLocationResponse(
    val city : String,
    val country : String,
    @SerializedName("regionName") val region: String,
    @SerializedName("lat") val latitude : Float,
    @SerializedName("lon") val longitude : Float,
    @SerializedName("zip") val postCode : String,
)