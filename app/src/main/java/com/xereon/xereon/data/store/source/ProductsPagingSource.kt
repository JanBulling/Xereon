package com.xereon.xereon.data.store.source

import androidx.paging.PagingSource
import com.xereon.xereon.data.products.SimpleProduct
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants

class ProductsPagingSource(
    private val api: XereonAPI,
    private val storeId: Int,
    private val query: String,
    private val sort: Constants.SortType = Constants.SortType.RESPONSE_NEW_FIRST
) : PagingSource<Int, SimpleProduct>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SimpleProduct> {
        val currentPage = params.key ?: 1

        return try {
            val response = api.getProductsFromStore(
                storeID = storeId,
                query = query,
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