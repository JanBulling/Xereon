package com.xereon.xereon.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.xereon.xereon.db.model.FavoriteStore
import com.xereon.xereon.db.model.OrderProduct
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [OrderProduct::class, FavoriteStore::class],
    version = 2
)
abstract class XereonDatabase : RoomDatabase() {

    abstract fun orderProductDao(): OrderProductDao
    abstract fun favoriteStoreDao(): FavoriteStoreDao

}