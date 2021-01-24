package com.xereon.xereon.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.xereon.xereon.db.model.OrderProduct
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [OrderProduct::class], version = 1)
abstract class XereonDatabase : RoomDatabase() {

    abstract fun orderProductDao(): OrderProductDao

    class Callback @Inject constructor(
        private val database: Provider<XereonDatabase>,
        private val applicationScope: CoroutineScope,
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            /*val dao = database.get().orderProductDao()

            applicationScope.launch {
                dao.insertOrderProducts(
                    OrderProduct(0, "Salami", 15.31f, 8, 1, 1, "Metzgerei Heußler"),
                    OrderProduct(2, "Magaritha", 14.1f, 2, 0, 5, "Pizaaaaaaa"),
                    OrderProduct(8, "Schinken", 13.1f, 0, 1, 1, "Metzgerei Heußler"),
                    OrderProduct(12, "Teddy Bär", 12.1f, 1, 1, 1612, "Steiff"),
                    OrderProduct(16516, "Hummel", 11.1f, 6, 1, 1612, "Steiff"),
                    OrderProduct(5,    "Leberkäse", 10.1f, 5, 0, 1, "Metzgerei Heußler"),
                    OrderProduct(156, "Buch", 9.1f, 3, 2, 8, "Thalia"),
                    OrderProduct(616, "Kochbuch", 8.1f, 0, 2, 8, "Thalia"),
                )
            }*/

        }
    }

}