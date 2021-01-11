package com.xereon.xereon.network

import com.xereon.xereon.BuildConfig
import com.xereon.xereon.data.model.*
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


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////   LOGIN / REGISTER   /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
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


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////   HOME / PRODUCTS / STORES   //////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/home/home.php")
    suspend fun getExploreData(
        @Query("id") userID: Int,
        @Query("postalcode") postalCode: String,
        @Query("version") version: Int
    ): ExploreData

    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/stores/store-information.php")
    suspend fun getStoreInformation(
        @Query("id") storeId: Int
    ): Store

    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/products/products.php")
    suspend fun getProductsFromStore(
        @Query("id") storeId: Int,
        @Query("search") query: String,
        @Query("order") productsOrder: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<SimpleProduct>

    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/products/product-information.php")
    suspend fun getProductDetails(
        @Query("id") productID: Int
    ): Product

    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/stores/get-stores-surroundings.php")
    suspend fun getStoresInArea(
        @Query("postalcode") zip: String
    ): List<LocationStore>


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////   SEARCH   /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/debug/home/main-search.php")
    suspend fun searchStoreName(
        @Query("name") query: String,
        @Query("postalcode") zip: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): List<SimpleStore>

    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/debug/home/main-search.php")
    suspend fun searchStoreByCategory(
        @Query("category") category: Int,
        @Query("postalcode") zip: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): List<SimpleStore>

    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/debug/home/main-search.php")
    suspend fun searchStoreByType(
        @Query("type") type: String,
        @Query("name") query: String,
        @Query("postalcode") zip: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): List<SimpleStore>

    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/debug/home/main-search.php")
    suspend fun searchStore(
        @Query("query") query: String,
        @Query("postalcode") zip: String,
        @Query("order") order: Int,
        @Query("filter") filter: Int,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): List<SimpleStore>
}