package com.xereon.xereon.data.repository

import android.util.Log
import androidx.paging.*
import com.xereon.xereon.R
import com.xereon.xereon.data.model.LocationStore
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.data.paging.ProductsPagingSource
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants.SortType
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.Resource
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepository @Inject constructor(private val xereonAPI: XereonAPI) {

    suspend fun getStore(storeId: Int) : Resource<Store> {
        return try {
            val response = xereonAPI.getStore(storeID = storeId)
            val result = response.body()
            if (response.isSuccessful && result != null)
                Resource.Success(result)
            else
                Resource.Error(R.string.unexprected_exception)

        } catch (e: Exception) {
            Log.e(TAG, "Error in Repository: ${e.stackTraceToString()}")
            when (e) {
                is HttpException -> Resource.Error(R.string.no_connection_exception)
                is IOException -> Resource.Error(R.string.no_connection_exception)
                else -> Resource.Error(R.string.unexprected_exception)
            }
        }
    }

    suspend fun getStoresInArea(zip: String) : Resource<List<LocationStore>> {
        return try {
            val response = xereonAPI.getStoresInArea(zip = zip)
            val result = response.body()
            if (response.isSuccessful && result != null)
                Resource.Success(result)
            else
                Resource.Error(R.string.unexprected_exception)

        } catch (e: Exception) {
            Log.e(TAG, "Error in Repository: ${e.stackTraceToString()}")
            when (e) {
                is HttpException -> Resource.Error(R.string.no_connection_exception)
                is IOException -> Resource.Error(R.string.no_connection_exception)
                else -> Resource.Error(R.string.unexprected_exception)
            }
        }
    }

    fun getProducts(storeID: Int, query: String, sort: SortType) =
        Pager(
            config = PagingConfig(
                initialLoadSize = 10,
                pageSize = 10,
                maxSize = 100,
                prefetchDistance = 1,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                ProductsPagingSource(
                    xereonAPI,
                    storeId = storeID,
                    query = query,
                    sort = sort) }
        ).liveData
}