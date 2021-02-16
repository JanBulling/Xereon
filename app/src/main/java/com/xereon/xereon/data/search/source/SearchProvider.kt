package com.xereon.xereon.data.search.source

import androidx.paging.PagedList
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.storage.LocalData
import com.xereon.xereon.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchProvider @Inject constructor(
    private val api: XereonAPI,
    private val localData: LocalData,
) {

    fun search(query: String, category: Int, sort: Constants.SortType) =
        Pager(
            config = PagingConfig(
                initialLoadSize = 20,
                pageSize = 20,
                maxSize = 100,
                prefetchDistance = 5,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                StorePagingSource(
                    api,
                    query = query,
                    postCode = localData.getPostCode(),
                    category = category,
                    sort = sort,
                )
            }
        ).liveData

    fun search(query: String, category: Int, type: String, sort: Constants.SortType) =
        Pager(
            config = PagingConfig(
                initialLoadSize = 20,
                pageSize = 20,
                maxSize = 100,
                prefetchDistance = 5,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                StorePagingSource(
                    api,
                    query = query,
                    postCode = localData.getPostCode(),
                    category = category,
                    type = type,
                    sort = sort,
                )
            }
        ).liveData
}