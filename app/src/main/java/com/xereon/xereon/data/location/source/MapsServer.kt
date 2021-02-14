package com.xereon.xereon.data.location.source

import android.util.Log
import com.xereon.xereon.R
import com.xereon.xereon.data.explore.ExploreData
import com.xereon.xereon.data.model.LocationStore
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Resource
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapsServer @Inject constructor(
    private val api: XereonAPI,
) {

    suspend fun getStoresInRegion(postCode: String) : Resource<List<LocationStore>> = try {
        val response = api.getStoresInArea(zip = postCode)
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

    companion object{
        private const val TAG = "MAPS_SERVER"
    }
}