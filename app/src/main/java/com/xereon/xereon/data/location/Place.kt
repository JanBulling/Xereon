package com.xereon.xereon.data.location

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class Place (
    @SerializedName("country") val country: String,
    @SerializedName("is_country") val isCountry: Boolean,
    @SerializedName("is_city") val isCity: Boolean,
    @SerializedName("postcode") val postcodesList: List<String>,
    @SerializedName("county") val countyList: List<String>,
    @SerializedName("administrative") val administrativeList: List<String>,
    @SerializedName("locale_names") val localeNamesList: List<String>,
    @SerializedName("city") val cityList: List<String>? = null,
    @SerializedName("_geoloc") val coordinates: GeoPosition
) {

    val name get() = if (localeNamesList.isNullOrEmpty()) "" else localeNamesList[0]
    val postCode get() = if (postcodesList.isNullOrEmpty()) "" else postcodesList[0]
    val administrative get() = if (administrativeList.isNullOrEmpty()) "" else administrativeList[0]
    val county get() = if (countyList.isNullOrEmpty()) "" else countyList[0]
    val city get() = if (cityList.isNullOrEmpty()) "" else "${cityList[0]}, "

    val latLng get() = LatLng(coordinates.latitude.toDouble(), coordinates.longitude.toDouble())

    override fun toString() =
        "$name ($postCode) in $country, $administrative, $county"

}

data class GeoPosition(
    @SerializedName("lat") val latitude: Float,
    @SerializedName("lng") val longitude: Float
)