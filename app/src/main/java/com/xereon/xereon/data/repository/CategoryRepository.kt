package com.xereon.xereon.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.xereon.xereon.R
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.data.paging.StoresPagingSource
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Resource
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val xereonAPI: XereonAPI) {

    suspend fun getExampleStoresForCategory(category: Int, zip: String) : Resource<List<SimpleStore>> {
        return try {
            val response = xereonAPI.searchStore(
                category = category,
                zip = zip,
                page = 1,
                limit = 15
            )
            val result = response.body()
            if (response.isSuccessful && result != null)
                Resource.Success(result)
            else
                Resource.Error(R.string.unexpected_exception)

        } catch (e: Exception) {
            Log.e(Constants.TAG, "Error in Repository: ${e.stackTraceToString()}")
            when (e) {
                is HttpException -> Resource.Error(R.string.no_connection_exception)
                is IOException -> Resource.Error(R.string.no_connection_exception)
                else -> Resource.Error(R.string.unexpected_exception)
            }
        }
    }

    fun getStoresByType(type: String, query: String, zip: String) =
        Pager(
            config = PagingConfig(
                initialLoadSize = 10,
                pageSize = 10,
                maxSize = 100,
                prefetchDistance = 1,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                StoresPagingSource(
                    xereonAPI,
                    query = query,
                    zip = zip,
                    type = type
                )
            }
        ).liveData

}