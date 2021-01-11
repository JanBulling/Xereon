package com.xereon.xereon.network

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * https://community.algolia.com/places/api-clients.html#rest-api
 *
 * alternative for later (algolia gets deprecated at 31.03.2022):
 * https://www.mapbox.com/place-search/
 */
interface AlgoliaPlacesApi {

    companion object {
        const val BASE_URL = "https://places-dsn.algolia.net/"
    }


    @POST("1/places/query")
    suspend fun getPlacesResults(
        @Body placesRequest: PlacesRequest
    ): PlacesResponse

}