package com.xereon.xereon.data.paging

import android.util.Log
import androidx.paging.PagingSource
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Constants.ORDER_DEFAULT
import kotlinx.coroutines.delay

class StoresPagingSource(
    private val xereonAPI: XereonAPI,
    private val query: String,  /*Type or name depending on the selected flag*/
    private val zip: String,
    private val type: String = "",
    private var category: Int? = null,
    private val orderStores: Int = ORDER_DEFAULT,
) : PagingSource<Int, SimpleStore>(){

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SimpleStore> {
        val currentPage = params.key ?: 1

        return try {
            if (category == -1)
                category = null

            val response = xereonAPI.searchStore(
                query = query,
                zip = zip,
                category = category,
                type = type,
                orderStores = orderStores,
                page = currentPage,
                limit = params.loadSize
            )

            LoadResult.Page(
                data = response,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (response.size < params.loadSize) null else currentPage + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}