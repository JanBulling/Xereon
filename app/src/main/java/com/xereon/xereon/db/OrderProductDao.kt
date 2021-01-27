package com.xereon.xereon.db

import android.os.Parcelable
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
    @Query("SELECT storeID, storeName, COUNT(id) as numberProducts, SUM(totalPrice) as sumPrice FROM shopping_cart_table GROUP BY storeID")
    fun getAllStores() : Flow<List<StoreBasic>>

    /**
     * @return Flow of List distinct stores
     */
    @Query("SELECT storeID, storeName, COUNT(id) as numberProducts, SUM(totalPrice) as sumPrice FROM shopping_cart_table WHERE storeID = :storeId GROUP BY storeID")
    fun getStoreById(storeId: Int) : Flow<StoreBasic>

    /**
     * @return Flow of List of Orders from a store
     */
    @Query("SELECT * FROM shopping_cart_table WHERE storeID = :storeID")
    fun getAllOrdersFromStore(storeID: Int) : Flow<List<OrderProduct>>


    @Query("SELECT SUM(totalPrice) FROM shopping_cart_table")
    fun getTotalPrice() : Flow<Float>


    /* delete order */
    @Delete
    suspend fun deleteOrder(order: OrderProduct)


    @Query("DELETE FROM shopping_cart_table WHERE storeID = :storeID")
    suspend fun deleteAllProductsFromStore(storeID: Int)
}

@Parcelize
data class StoreBasic(
    @ColumnInfo(name = "storeID") val id: Int = -1,
    @ColumnInfo(name = "storeName") val name: String = "",
    val numberProducts: Int = 0,
    val sumPrice: Float = 0f,
) : Parcelable {
    val logoImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/logo/$id.png"
}