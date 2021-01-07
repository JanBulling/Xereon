package com.xereon.xereon.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.xereon.xereon.data.paging.StoresPagingSource
import com.xereon.xereon.network.XereonAPI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val xereonAPI: XereonAPI
) {

    fun searchStoreByName(query: String, zip: String) =
        Pager(
            config = PagingConfig(
                initialLoadSize = 20,
                pageSize = 20,
                maxSize = 100,
                prefetchDistance = 5,
                enablePlaceholders = false
            ), pagingSourceFactory = { StoresPagingSource(xereonAPI, query, zip) }
        ).liveData

}