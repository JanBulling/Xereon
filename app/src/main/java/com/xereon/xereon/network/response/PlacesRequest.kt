package com.xereon.xereon.network.response

import com.google.gson.annotations.SerializedName

data class PlacesRequest(
    @SerializedName("query") val query: String,
    @SerializedName("type") val type: List<String> = listOf(
        TYPE_CITY,
        TYPE_ADDRESS
    ),
    @SerializedName("countries") val countries: String = COUNTRY_GERMANY,
    @SerializedName("language") val language: String = LANGUAGE_GERMAN,
    @SerializedName("hitsPerPage") val hitsPerPage: Int = 4
) {

    companion object {
        const val TYPE_CITY = "city"
        const val TYPE_COUNTRY = "country"
        const val TYPE_ADDRESS = "address"

        const val LANGUAGE_GERMAN = "de"
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_FRENCH = "fr"
        const val LANGUAGE_SPANISH = "es"

        const val COUNTRY_GERMANY = "de"
        const val COUNTRY_US = "en"
        const val COUNTRY_FRANCE = "fr"
        const val COUNTRY_SPAIN = "es"
    }
}