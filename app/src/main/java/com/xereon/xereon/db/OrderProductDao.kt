package com.xereon.xereon.db

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*
import com.xereon.xereon.db.model.OrderProduct
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderProductDao {

    /* Insert new order to table */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderProduct)

    /* Update order in table */
    @Update
    suspend fun updateOrder(order: OrderProduct)

    /**
     * @return Flow of List of OrderProducts
     */
    @Query("SELECT * FROM shopping_cart_table ORDER BY storeName ASC")
    fun getAllOrders(): Flow<List<OrderProduct>>

    /**
     * @return number of distinct orders
     */
    @Query("SELECT COUNT(id) FROM shopping_cart_table")
    fun getNumberOrders() : Flow<Int>

    /**
     * @return Flow of List distinct stores
     */
    @Query("SELECT storeID, storeName, COUNT(id) as numberProducts, SUM(completePrice) as sumPrice FROM shopping_cart_table GROUP BY storeID")
    fun getAllStores() : Flow<List<StoreBasic>>

    /**
     * @return Flow of List of Orders from a store
     */
    @Query("SELECT id, name, price, completePrice, unit, count FROM shopping_cart_table WHERE storeID = :storeID")
    fun getAllOrdersFromStore(storeID: Int) : Flow<List<SimpleOrder>>

    @Query("SELECT SUM(completePrice) FROM shopping_cart_table")
    fun getTotalPrice() : Flow<Float>

    /* delete order */
    @Delete
    suspend fun deleteOrder(order: OrderProduct)
}

@Parcelize
data class StoreBasic(
    @ColumnInfo(name = "storeID") val storeID: Int? = -1,
    @ColumnInfo(name = "storeName") val storeName: String?,
    val numberProducts: Int?,
    val sumPrice: Float?,
) : Parcelable {
    val logoImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/logo/$storeID.png"
}

@Parcelize
data class SimpleOrder(
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "price") val price: Float = 0f,
    @ColumnInfo(name = "completePrice") val completePrice: Float = 0f,
    @ColumnInfo(name = "unit") val unit: Int = 0,
    @ColumnInfo(name = "count") val count: Int = 0,
) : Parcelable {
    val productImageURL get() = "http://vordertuer.bplaced.net/app-img/products/" + id + ".png"
}