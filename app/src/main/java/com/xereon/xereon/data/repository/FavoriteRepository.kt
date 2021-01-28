package com.xereon.xereon.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.xereon.xereon.db.FavoriteStoreDao
import com.xereon.xereon.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(private val dao: FavoriteStoreDao) {

    fun getFavorites(sort: Constants.SortTypes) =
        Pager(
            config = PagingConfig(
                initialLoadSize = 10,
                pageSize = 10,
                maxSize = 100,
                prefetchDistance = 1,
                enablePlaceholders = false
            ), pagingSourceFactory = { dao.getAllOrdersSorted(sort) }
        ).liveData

}