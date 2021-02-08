package com.xereon.xereon.data.model

import com.google.android.gms.maps.model.LatLng
import com.xereon.xereon.data.model.places.GooglePlacesData

data class Store(
    val id: Int,
    val name: String,
    val description: String = "----",
    val latitude: Float,
    val longitude: Float,
    val address: String,
    val city: String,
    val phone: String = "-",
    val website: String = "-",
    val openinghours: Array<String>,
    val type: String,
    val category: Int,
    val preorder: Boolean,
    val nsfw: Boolean,

    var placesData: GooglePlacesData?,
) {
    val logoImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/logo/$id.png"
    val officeImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/store/$id"
    val completeAddress get() = "$address, $city"
    val coordinates get() = LatLng(latitude.toDouble(), longitude.toDouble())

    val rating: Float get() = placesData?.rating ?: 0f
    val numberRating: Int get() = placesData?.numberRating ?: 0
    val completeRating: String get() = "${rating}     (${numberRating})"

    override fun toString(): String {
        return "Id: $id, Name: $name, Descr: $description, lat: $latitude, " +
                "lng: $longitude, address: $completeAddress, website: $website, " +
                "phone: $phone, type: $type, category: $category"
    }
}