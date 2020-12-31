package com.xereon.xereon.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.*
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.data.paging.StorePagingSource
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepository @Inject constructor(private val xereonAPI: XereonAPI) {

    suspend fun getStoreData(apiKey: String, storeID: Int): Flow<DataState<Store>> = flow {
        emit(DataState.Loading)
        try {

            val call = xereonAPI.getStoreInformationCall(apiKey, storeID)
            call.enqueue(object: Callback<Store> {
                override fun onFailure(call: Call<Store>, t: Throwable) {
                    Log.d("[APP_DEBUG]", "onFailure: " + t.message)
                }

                override fun onResponse(call: Call<Store>, response: Response<Store>) {
                    Log.d("[APP_DEBUG]", "onResponse: " + response.isSuccessful + " " + response.message())
                }
            })


            val networkStoreData = xereonAPI.getStoreInformation(apiKey, storeID)
            //Log.d("[APP_DEBUG]", "getStoreData() finished loading (${networkStoreData.name})")

            emit(DataState.Success(networkStoreData))
        } catch (e : IOException) {
            emit(DataState.Error(e))
        } catch (e : HttpException) {
            emit(DataState.Error(e))
        }
    }

    suspend fun getAllProducts(apiKey: String, storeID: Int): Flow<DataState<List<SimpleProduct>>> = flow {
        emit(DataState.Loading)
        try {
            val networkAllProducts = xereonAPI.getProductsFromStore(apiKey, storeID, 1, 100, "")

            emit(DataState.Success(networkAllProducts))
        } catch (e : IOException) {
            emit(DataState.Error(e))
        } catch (e : HttpException) {
            emit(DataState.Error(e))
        }
    }

    fun searchProduct(apiKey: String, storeID: Int, query: String): LiveData<PagingData<SimpleProduct>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 100,
                prefetchDistance = 1,
                enablePlaceholders = false
            ), pagingSourceFactory = { StorePagingSource(xereonAPI, apiKey, storeID, query) }
        ).liveData
    }
}