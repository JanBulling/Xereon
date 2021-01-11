package com.xereon.xereon.data.model.places

import com.google.gson.annotations.SerializedName

data class Place (
    @SerializedName("country") val country: String,
    @SerializedName("is_country") val isCountry: Boolean,
    @SerializedName("is_city") val isCity: Boolean,
    @SerializedName("postcode") val postCodes: List<String>,
    @SerializedName("county") val county: List<String>,
    @SerializedName("administrative") val administrative: List<String>,
    @SerializedName("locale_names") val locale_names: List<String>,
    @SerializedName("city") val city: List<String>? = null,
    @SerializedName("_geoloc") val coordinares: GeoPosition
) {

    val locationName get() = if (locale_names.isNullOrEmpty()) "" else locale_names[0]
    val mainPostalCode get() = if (postCodes.isNullOrEmpty()) "" else postCodes[0]
    val mainAdministrative get() = if (administrative.isNullOrEmpty()) "" else administrative[0]
    val mainCounty get() = if (county.isNullOrEmpty()) "" else county[0]

    override fun toString() =
        "$locationName ($mainPostalCode) in $country, $mainAdministrative, $mainCounty"

}

data class GeoPosition(
    @SerializedName("lat") val latitude: Float,
    @SerializedName("lng") val longitude: Float
)