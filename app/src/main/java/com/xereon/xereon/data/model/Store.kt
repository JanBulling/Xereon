package com.xereon.xereon.data.model

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
    val openinghours: String,
    val type: String,
    val category: Int,
    val preorder: Boolean,
    val nsfw: Boolean
) {
    val logoImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/logo/$id.png"
    val officeImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/store/$id"
    val completeAddress get() = "$address, $city"

    override fun toString(): String {
        return "Id: $id, Name: $name, Descr: $description, lat: $latitude, " +
                "lng: $longitude, address: $completeAddress, website: $website, " +
                "phone: $phone, open: $openinghours, type: $type, category: $category"
    }
}