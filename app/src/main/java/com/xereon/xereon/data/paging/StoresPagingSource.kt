package com.xereon.xereon.data.paging

import androidx.paging.PagingSource
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.network.XereonAPI
import kotlinx.coroutines.delay

class StoresPagingSource(
    private val xereonAPI: XereonAPI,
    private val query: String,
    private val zip: String
) : PagingSource<Int, SimpleStore>(){


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SimpleStore> {
        val currentPage = params.key ?: 1

        return try {
            val response = xereonAPI.searchStoreName(query, zip, params.loadSize, currentPage)

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