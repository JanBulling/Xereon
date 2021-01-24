package com.xereon.xereon.data.repository

import com.xereon.xereon.network.AlgoliaPlacesApi
import com.xereon.xereon.network.response.PlacesRequest
import com.xereon.xereon.network.response.PlacesResponse
import com.xereon.xereon.util.Resource
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesRepository @Inject constructor(private val algoliaPlacesApi: AlgoliaPlacesApi) {

    suspend fun getPlaces(placesRequest: PlacesRequest) : Resource<PlacesResponse> {
        return try {
            val response = algoliaPlacesApi.getPlacesResults(placesRequest)
            val result = response.body()
            if (response.isSuccessful && result != null)
                Resource.Success(result)
            else
                Resource.Error(response.message())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ein unerwarteter Fehler ist aufgetreten")
        }
    }

}