package com.xereon.xereon.data.maps

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class MapsData(
    @SerializedName("latitude") val lat: Float,
    @SerializedName("longitude") val lng: Float,
    @SerializedName("zoom") val zoom: Float,
    @SerializedName("postCode") val postCode: String
) {

    val latLng get() = LatLng(lat.toDouble(), lng.toDouble())

}