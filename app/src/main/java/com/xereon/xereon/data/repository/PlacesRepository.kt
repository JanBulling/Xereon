package com.xereon.xereon.data.repository

import android.util.Log.d
import com.xereon.xereon.network.AlgoliaPlacesApi
import com.xereon.xereon.network.PlacesRequest
import com.xereon.xereon.network.PlacesResponse
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesRepository @Inject constructor(private val algoliaPlacesApi: AlgoliaPlacesApi) {

    suspend fun getPlaces(placesRequest: PlacesRequest): Flow<DataState<PlacesResponse>> = flow {
        try {
            val networkExplore = algoliaPlacesApi.getPlacesResults(placesRequest)

            emit(DataState.Success(networkExplore))
        } catch (e : Exception) {
            d(TAG, "ERROR: ${e.printStackTrace()}")
            when (e) {
                is IOException, is HttpException -> emit(DataState.Error("Keine Verbindung"))
                else -> emit(DataState.Error("Es ist ein Fehler aufgetreten"))
            }
        }
    }

}