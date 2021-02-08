package com.xereon.xereon.data.model.places

import com.google.gson.annotations.SerializedName

//https://github.com/m-wrzr/populartimes
data class GooglePlacesData(
    @SerializedName("rating")
    val rating: Float = 0f,

    @SerializedName("rating_n")
    val numberRating: Int = 0,

    @SerializedName("current_popularity")
    val currentPopularity: Int = 0,

    @SerializedName("populartimes")
    val popularTimes: List<Array<Int>>
)