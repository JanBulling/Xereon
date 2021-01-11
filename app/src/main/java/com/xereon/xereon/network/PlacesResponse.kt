package com.xereon.xereon.network

import com.google.gson.annotations.SerializedName
import com.xereon.xereon.data.model.places.Place

data class PlacesResponse(
    @SerializedName("hits") val hits: List<Place>,
    @SerializedName("nbHits") val numberHits: Int,
    @SerializedName("processingTimeMS") val processTime: Int,
    @SerializedName("query") val query: String,
) {
}