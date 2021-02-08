package com.xereon.xereon.network

import com.xereon.xereon.network.response.IPLocationResponse
import retrofit2.Response
import retrofit2.http.GET

interface IPLocationAPI {

    companion object {
        const val BASE_URL = "http://ip-api.com/"
    }

    @GET("json")
    suspend fun getLocationWithIP(): Response<IPLocationResponse>
}