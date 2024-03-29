package com.xereon.xereon.data.store

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xereon.xereon.data.store.SimpleStore

@Entity(tableName = "favorite_store_table")
data class FavoriteStore(
    @PrimaryKey(autoGenerate = false) val id: Int = 0,
    val name : String = "",
    val city: String = "",
    val type : String = "",
    val category : Int = -1,
    val insertDate: Long = 0,
) {
    val logoImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/logo/$id.png"
    val officeImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/store/$id"

    override fun toString() = "id: $id, name: $name, type: $type with category: $category"

    fun toSimpleStore() =
        SimpleStore(id, name, city, type, category)
}