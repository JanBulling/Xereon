package com.xereon.xereon.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_cart_table")
data class OrderProduct(
    val id: Int,
    val name: String,
    val price: Float,
    val completePrice: Float,
    val unit: Int,
    val count: Int,
    val storeID: Int,
    val storeName: String,
    @PrimaryKey(autoGenerate = true) val table_id: Int = 0
) {
    override fun toString() =
        "$name ($id) was ordered $count-times (price: $price$unit) from $storeName ($storeID)"
}