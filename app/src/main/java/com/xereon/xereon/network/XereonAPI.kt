package com.xereon.xereon.network

import com.xereon.xereon.BuildConfig
import com.xereon.xereon.data.model.ExploreData
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.data.model.Store
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface XereonAPI {

    companion object {
        const val ACCESS_KEY = BuildConfig.XEREON_ACCESS_KEY
        const val BASE_URL = "http://vordertuer.bplaced.net/"
        const val IP_LOCATION_BASE_URL = ""
    }

    @GET("app-php/users/login.php")
    suspend fun login(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("firebasetoken") token: String
    ): XereonResponse

    @GET("app-php/users/create-user.php")
    suspend fun register(
        @Query("name") name: String,
        @Query("email") emailAddress: String,
        @Query("password") password: String,
        @Query("firebasetoken") token: String
    ): XereonResponse

    @GET("app-php/users/reset-password.php")
    suspend fun resetPassword(@Query("email") emailAddress: String): XereonResponse

    @GET("json")
    suspend fun getLocationWithIP(): IPLocationResponse


    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/home/home.php")
    suspend fun getExploreData(
        @Header("API") API_KEY: String,
        @Query("id") userID: Int,
        @Query("postalcode") postalCode: String,
        @Query("version") version: Int
    ): ExploreData

    @GET("app-php/stores/store-information.php")
    suspend fun getStoreInformation(
        @Header("API") API_KEY: String,
        @Query("id") storeId: Int
    ): Store

    @GET("app-php/products/products.php")
    suspend fun getProductsFromStore(
        @Header("API") API_KEY: String,
        @Query("id") storeId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("search") query: String
    ): List<SimpleProduct>
}