package com.xereon.xereon.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.*
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.data.paging.StorePagingSource
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.utils.DataState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepository @Inject constructor(private val xereonAPI: XereonAPI) {

    suspend fun getStoreData(apiKey: String, storeID: Int): Flow<DataState<Store>> = flow {
        emit(DataState.Loading)
        try {
            delay(1500)
            val networkStoreData = xereonAPI.getStoreInformation(apiKey, storeID)

            emit(DataState.Success(networkStoreData))
        } catch (e : Exception) {
            when (e) {
                is IOException, is HttpException -> emit(DataState.Error("Keine Verbindung"))
                else -> emit(DataState.Error("Es ist ein Fehler unterlaufen"))
            }
        }
    }

    fun searchProduct(apiKey: String, storeID: Int, query: String): LiveData<PagingData<SimpleProduct>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = 10,
                pageSize = 10,
                maxSize = 100,
                prefetchDistance = 1,
                enablePlaceholders = false
            ), pagingSourceFactory = { StorePagingSource(xereonAPI, apiKey, storeID, query) }
        ).liveData
    }
}