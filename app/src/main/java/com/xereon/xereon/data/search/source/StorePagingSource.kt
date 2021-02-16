package com.xereon.xereon.data.search.source

import androidx.paging.PagingSource
import com.xereon.xereon.data.store.SimpleStore
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants

class StorePagingSource(
    private val api: XereonAPI,
    private val query: String,
    private val postCode: String,
    private val category: Int = -1,
    private val type: String = "",
    private val sort: Constants.SortType = Constants.SortType.RESPONSE_NEW_FIRST
) : PagingSource<Int, SimpleStore>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SimpleStore> {
        val currentPage = params.key ?: 1

        val category: Int? = if (category == -1) null else category
        val type: String? = if (type.isBlank()) null else type

        return try {
            val response = api.searchStore(
                query = query,
                zip = postCode,
                category = category,
                type = type,
                sort = sort.index,
                page = currentPage,
                limit = params.loadSize
            )
            val result = response.body()
            if (!response.isSuccessful || result == null)
                return LoadResult.Error(Throwable(response.message()))

            LoadResult.Page(
                data = result,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (result.size < params.loadSize) null else currentPage + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}