package com.xereon.xereon.data.model

data class SimpleStore(
    val id : Int,
    val name : String,
    val type : String,
    val category : Int
) {
    val logoImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/logo/$id.png"
    val officeImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/store/$id"

    override fun toString() = "id: $id, name: $name, type: $type with category: $category"
}