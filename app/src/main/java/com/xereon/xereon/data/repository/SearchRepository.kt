package com.xereon.xereon.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.xereon.xereon.data.paging.StoresPagingSource
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val xereonAPI: XereonAPI
) {

    fun searchStore(query: String, zip: String, category: Int?, sort: Constants.SortTypes) =
        Pager(
            config = PagingConfig(
                initialLoadSize = 10,
                pageSize = 10,
                maxSize = 100,
                prefetchDistance = 5,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                StoresPagingSource(
                    xereonAPI,
                    query = query,
                    zip = zip,
                    category = category,
                    sort = sort
                )
            }
        ).liveData
}