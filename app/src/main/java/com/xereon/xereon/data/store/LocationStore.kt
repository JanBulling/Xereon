package com.xereon.xereon.data.store

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.xereon.xereon.data.store.Store

data class LocationStore(
    val id : Int,
    val name : String,
    val latitude: Float,
    val longitude: Float,
    val type: String,
    val category : Int
) : ClusterItem {
    val logoImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/logo/$id.png"
    val officeImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/store/$id"

    val latLng get() = LatLng(latitude.toDouble(), longitude.toDouble())

    override fun toString() = "id: $id, name: $name in category: $category"

    override fun getTitle() = name
    override fun getPosition() = latLng
    override fun getSnippet() = type

    fun toStore() =
        Store(
            id, name, "---", latitude, longitude, "--", "--", "--",
            "--", arrayOf("", "", "", "", "", "", ""), type, category, true, false, emptyList()
        )
}