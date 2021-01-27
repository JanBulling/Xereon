package com.xereon.xereon.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.xereon.xereon.db.model.OrderProduct
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [OrderProduct::class],
    version = 1
)
abstract class XereonDatabase : RoomDatabase() {

    abstract fun orderProductDao(): OrderProductDao

}