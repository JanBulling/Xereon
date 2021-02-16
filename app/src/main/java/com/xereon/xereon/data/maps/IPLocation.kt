package com.xereon.xereon.data.maps

import com.google.gson.annotations.SerializedName

data class IPLocation(
    val city : String,
    val country : String,
    @SerializedName("regionName") val region: String,
    @SerializedName("lat") val latitude : Float,
    @SerializedName("lon") val longitude : Float,
    @SerializedName("zip") val postCode : String,
)