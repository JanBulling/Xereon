package com.xereon.xereon.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.xereon.xereon.data.model.LocationStore
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.data.paging.ProductsPagingSource
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.DataState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepository @Inject constructor(private val xereonAPI: XereonAPI) {

    suspend fun getStoreData(storeID: Int): Flow<DataState<Store>> = flow {
        emit(DataState.Loading)
        try {
            delay(200)
            val networkStoreData = xereonAPI.getStoreInformation(storeID)

            emit(DataState.Success(networkStoreData))
        } catch (e : Exception) {
            when (e) {
                is IOException, is HttpException -> emit(DataState.Error("Keine Verbindung"))
                else -> emit(DataState.Error("Es ist ein Fehler aufgetreten"))
            }
        }
    }

    fun searchProduct(storeID: Int, query: String) =
        Pager(
            config = PagingConfig(
                initialLoadSize = 10,
                pageSize = 10,
                maxSize = 100,
                prefetchDistance = 1,
                enablePlaceholders = false
            ), pagingSourceFactory = { ProductsPagingSource(xereonAPI, storeID, query) }
        ).liveData

    suspend fun getStoresInArea(zip: String) : Flow<DataState<List<LocationStore>>> = flow {
        emit(DataState.Loading)
        try {
            delay(200)
            val networkState = xereonAPI.getStoresInArea(zip)

            emit(DataState.Success(networkState))
        } catch (e: Exception) {
            when (e) {
                is IOException, is HttpException -> emit(DataState.Error("Keine Verbindung"))
                else -> emit(DataState.Error("Es ist ein Fehler aufgetreten"))
            }
        }
    }
}