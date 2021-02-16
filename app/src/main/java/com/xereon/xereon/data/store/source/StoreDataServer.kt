package com.xereon.xereon.data.store.source

import android.util.Log
import com.xereon.xereon.R
import com.xereon.xereon.data.store.Store
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Resource
import dagger.Reusable
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

@Reusable
class StoreDataServer @Inject constructor(
    private val api: XereonAPI,
) {

    suspend fun getStoreData(storeId: Int) : Resource<Store> = try {
        delay(150)

        val response = api.getStore(storeID = storeId)
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