package com.xereon.xereon.data.repository

import android.util.Log
import com.xereon.xereon.R
import com.xereon.xereon.network.AlgoliaPlacesApi
import com.xereon.xereon.network.IPLocationAPI
import com.xereon.xereon.network.response.IPLocationResponse
import com.xereon.xereon.network.response.PlacesRequest
import com.xereon.xereon.network.response.PlacesResponse
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.Resource
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesRepository @Inject constructor(
    private val algoliaPlacesApi: AlgoliaPlacesApi,
    private val ipLocationAPI: IPLocationAPI,
) {

    suspend fun getPlaces(placesRequest: PlacesRequest) : Resource<PlacesResponse> {
        return try {
            val response = algoliaPlacesApi.getPlacesResults(placesRequest)
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

    suspend fun getApproximatePosition() : Resource<IPLocationResponse> {
        return try {
            val response = ipLocationAPI.getLocationWithIP()
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

}