package com.xereon.xereon.data.repository

import androidx.paging.*
import com.xereon.xereon.data.model.LocationStore
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.data.paging.ProductsPagingSource
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants.SortTypes
import com.xereon.xereon.util.Resource
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepository @Inject constructor(private val xereonAPI: XereonAPI) {

    suspend fun getStore(storeId: Int) : Resource<Store> {
        return try {
            val response = xereonAPI.getStore(storeId = storeId)
            val result = response.body()
            if (response.isSuccessful && result != null)
                Resource.Success(result)
            else
                Resource.Error(response.message())

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ein unerwarteter Fehler ist aufgetreten")
        }
    }

    suspend fun getStoresInArea(zip: String) : Resource<List<LocationStore>> {
        return try {
            val response = xereonAPI.getStoresInArea(zip = zip)
            val result = response.body()
            if (response.isSuccessful && result != null)
                Resource.Success(result)
            else
                Resource.Error(response.message())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ein unerwarteter Fehler ist aufgetreten")
        }
    }

    fun getProducts(storeID: Int, query: String, sort: SortTypes) =
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


    /*suspend fun getStoreData(storeID: Int): Flow<DataState<Store>> = flow {
        emit(DataState.Loading)
        try {
            val networkStoreData = xereonAPI.getStore(storeID)

            emit(DataState.Success(networkStoreData))
        } catch (e : Exception) {
            when (e) {
                is IOException, is HttpException -> emit(DataState.Error("Keine Verbindung"))
                else -> emit(DataState.Error("Es ist ein Fehler aufgetreten"))
            }
        }
    }*/


    /*suspend fun getStoresInArea(zip: String) : Flow<DataState<List<LocationStore>>> = flow {
        emit(DataState.Loading)
        try {
            val networkState = xereonAPI.getStoresInArea(zip)

            emit(DataState.Success(networkState))
        } catch (e: Exception) {
            when (e) {
                is IOException, is HttpException -> emit(DataState.Error("Keine Verbindung"))
                else -> emit(DataState.Error("Es ist ein Fehler aufgetreten"))
            }
        }
    }*/
}