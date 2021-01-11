package com.xereon.xereon.data.paging

import androidx.paging.PagingSource
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants.ORDER_DEFAULT
import kotlinx.coroutines.delay

class ProductsPagingSource(
    private val xereonAPI: XereonAPI,
    private val storeId: Int,
    private val query: String,
    private val productsOrder: Int = ORDER_DEFAULT
) : PagingSource<Int, SimpleProduct>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SimpleProduct> {
        val currentPage = params.key ?: 1

        return try {
            delay(250)
            val response = xereonAPI.getProductsFromStore(storeId, query, productsOrder, currentPage, params.loadSize)

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