package com.xereon.xereon.data.paging

import androidx.paging.PagingSource
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants.SortTypes

class StoresPagingSource(
    private val xereonAPI: XereonAPI,
    private val query: String,  /*Type or name depending on the selected flag*/
    private val zip: String,
    private val type: String = "",
    private var category: Int? = null,
    private val sort: SortTypes = SortTypes.SORT_RESPONSE_DEFAULT,
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