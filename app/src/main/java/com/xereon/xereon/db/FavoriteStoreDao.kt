package com.xereon.xereon.db

import androidx.paging.PagingSource
import androidx.room.*
import com.xereon.xereon.db.model.FavoriteStore
import com.xereon.xereon.util.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteStoreDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteStore)

    /* Update order in table */
    @Update
    suspend fun updateOrder(favorite: FavoriteStore)

    /**
     * @return Flow of List of FavoriteStores
     */
    @Query("SELECT * FROM favorite_store_table ORDER BY insertDate ASC")
    fun getAllOrdersASC(): PagingSource<Int, FavoriteStore>

    @Query("SELECT * FROM favorite_store_table ORDER BY insertDate DESC")
    fun getAllOrdersDESC(): PagingSource<Int, FavoriteStore>

    @Query("SELECT * FROM favorite_store_table ORDER BY name ASC")
    fun getAllOrdersAZ(): PagingSource<Int, FavoriteStore>

    @Query("SELECT * FROM favorite_store_table ORDER BY name DESC")
    fun getAllOrdersZA(): PagingSource<Int, FavoriteStore>

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteStore)

    @Query("DELETE FROM favorite_store_table")
    suspend fun deleteAllFavorites()

    fun getAllOrdersSorted(sortType: Constants.SortTypes): PagingSource<Int, FavoriteStore> =
        when (sortType) {
            Constants.SortTypes.SORT_RESPONSE_NEW_FIRST -> getAllOrdersDESC()
            Constants.SortTypes.SORT_RESPONSE_OLD_FIRST -> getAllOrdersASC()
            Constants.SortTypes.SORT_RESPONSE_A_Z -> getAllOrdersAZ()
            Constants.SortTypes.SORT_RESPONSE_Z_A -> getAllOrdersZA()
            else ->
                getAllOrdersASC()
        }
}