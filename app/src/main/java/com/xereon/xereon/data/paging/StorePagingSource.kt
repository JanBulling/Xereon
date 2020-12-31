package com.xereon.xereon.data.paging

import android.util.Log
import androidx.paging.PagingSource
import com.bumptech.glide.load.HttpException
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.network.XereonAPI
import java.io.IOException

class StorePagingSource(
    private val xereonAPI: XereonAPI,
    private val api_key: String,
    private val storeId: Int,
    private val query: String
) : PagingSource<Int, SimpleProduct>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SimpleProduct> {
        val currentPage = params.key ?: 1

        return try {
            val response = xereonAPI.getProductsFromStore(api_key, storeId, currentPage, params.loadSize, query)
            Log.d("[APP_DEBUG]", "loadProducts (page $currentPage)")

            LoadResult.Page(
                data = response,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (response.isEmpty()) null else currentPage + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}