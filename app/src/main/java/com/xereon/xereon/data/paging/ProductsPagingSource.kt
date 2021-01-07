package com.xereon.xereon.data.paging

import android.util.Log
import androidx.paging.PagingSource
import com.bumptech.glide.load.HttpException
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.network.XereonAPI
import kotlinx.coroutines.delay
import java.io.IOException

class ProductsPagingSource(
    private val xereonAPI: XereonAPI,
    private val storeId: Int,
    private val query: String
) : PagingSource<Int, SimpleProduct>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SimpleProduct> {
        val currentPage = params.key ?: 1

        return try {
            delay(1000)
            val response = xereonAPI.getProductsFromStore(storeId, currentPage, params.loadSize, query)

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