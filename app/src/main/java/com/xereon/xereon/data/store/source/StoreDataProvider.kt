package com.xereon.xereon.data.store.source

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.xereon.xereon.data.paging.ProductsPagingSource
import com.xereon.xereon.data.store.Store
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreDataProvider @Inject constructor(
    private val server: StoreDataServer,
    private val api: XereonAPI
) {

    suspend fun getStoreData(storeId: Int): Resource<Store> = server.getStoreData(storeId)

    fun getProducts(storeID: Int, query: String, sort: Constants.SortType) =
        Pager(
            config = PagingConfig(
                initialLoadSize = 10,
                pageSize = 10,
                maxSize = 100,
                prefetchDistance = 1,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                ProductsPagingSource(
                    api,
                    storeId = storeID,
                    query = query,
                    sort = sort) }
        ).liveData
}