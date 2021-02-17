package com.xereon.xereon.data.favorites.source

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.xereon.xereon.data.store.FavoriteStore
import com.xereon.xereon.storage.FavoriteStoreDao
import com.xereon.xereon.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesProvider @Inject constructor(
    private val dao: FavoriteStoreDao
) {

    fun getFavorites(sortType: Constants.SortType) = Pager(
        config = PagingConfig(
            initialLoadSize = 20,
            pageSize = 20,
            maxSize = 100,
            prefetchDistance = 5,
            enablePlaceholders = false
        ), pagingSourceFactory = { dao.getAllOrdersSorted(sortType) }
    ).liveData

    suspend fun deleteAllFavorites() = dao.deleteAllFavorites()
    suspend fun deleteFavorite(favoriteStore: FavoriteStore) = dao.deleteFavorite(favoriteStore)
    suspend fun insertFavorite(favoriteStore: FavoriteStore) = dao.insert(favoriteStore)
}