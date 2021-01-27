package com.xereon.xereon.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_cart_table")
data class OrderProduct(
    val id: Int = -1,
    val name: String = "",
    val price: Float = 0f,
    val totalPrice: Float = 0f,
    val unit: Int = -1,
    val count: Int = 0,
    val storeID: Int = -1,
    val storeName: String = "",
    @PrimaryKey(autoGenerate = true) val table_id: Int = 0
) {
    val productImageURL get() = "http://vordertuer.bplaced.net/app-img/products/" + id + ".png"

    override fun toString() =
        "$name ($id) was ordered $count-times (price: $price$unit) from $storeName ($storeID)"
}