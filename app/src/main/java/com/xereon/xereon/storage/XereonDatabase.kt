package com.xereon.xereon.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xereon.xereon.data.store.FavoriteStore
import com.xereon.xereon.data.products.OrderProduct

@Database(
    entities = [OrderProduct::class, FavoriteStore::class],
    version = 2
)
abstract class XereonDatabase : RoomDatabase() {

    abstract fun orderProductDao(): OrderProductDao
    abstract fun favoriteStoreDao(): FavoriteStoreDao

}