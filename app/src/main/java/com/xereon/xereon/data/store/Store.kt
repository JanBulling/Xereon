package com.xereon.xereon.data.store

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.xereon.xereon.data.model.places.GooglePlacesData
import com.xereon.xereon.data.products.SimpleProduct

data class Store(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String = "----",
    @SerializedName("latitude") val latitude: Float,
    @SerializedName("longitude") val longitude: Float,
    @SerializedName("address") val address: String,
    @SerializedName("city") val city: String,
    @SerializedName("phone") val phone: String = "-",
    @SerializedName("website") val website: String = "-",
    @SerializedName("openinghours") val openinghours: Array<String>,
    @SerializedName("type") val type: String,
    @SerializedName("category") val category: Int,
    @SerializedName("preorder") val allowOrdering: Boolean,
    @SerializedName("nsfw") val adultContent: Boolean,
    @SerializedName("exampleProducts") val exampleProducts: List<SimpleProduct>,
) {
    val logoImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/logo/$id.png"
    val officeImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/store/$id"
    val completeAddress get() = "$address, $city"
    val coordinates get() = LatLng(latitude.toDouble(), longitude.toDouble())

    val latLng get() = LatLng(latitude.toDouble(), longitude.toDouble())

    override fun toString(): String {
        return "Id: $id, Name: $name, Descr: $description, lat: $latitude, " +
                "lng: $longitude, address: $completeAddress, website: $website, " +
                "phone: $phone, type: $type, category: $category"
    }
}