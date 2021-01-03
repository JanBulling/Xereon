package com.xereon.xereon.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SimpleStore(
    var id : Int,
    val name : String,
    val type : String,
    val category : Int
) : Parcelable {
    val logoImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/logo/$id.png"
    val officeImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/store/$id"

    override fun toString() = "id: $id, name: $name, type: $type with category: $category"
}