package com.xereon.xereon.data.store

import android.os.Parcelable
import com.xereon.xereon.db.model.FavoriteStore
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SimpleStore(
    var id : Int,
    val name : String,
    val city: String,
    val type : String,
    val category : Int
) : Parcelable {
    val logoImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/logo/$id.png"
    val officeImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/store/$id"

    override fun toString() = "id: $id, name: $name, type: $type with category: $category"

    fun toFavoriteStore() = FavoriteStore(id, name, city, type, category)
}