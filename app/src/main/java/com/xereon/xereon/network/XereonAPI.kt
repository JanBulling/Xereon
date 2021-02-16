package com.xereon.xereon.network

import com.xereon.xereon.BuildConfig
import com.xereon.xereon.data.explore.ExploreData
import com.xereon.xereon.data.login.LoginResponse
import com.xereon.xereon.data.model.*
import com.xereon.xereon.data.products.Product
import com.xereon.xereon.data.products.SimpleProduct
import com.xereon.xereon.data.store.LocationStore
import com.xereon.xereon.data.store.SimpleStore
import com.xereon.xereon.data.store.Store
import com.xereon.xereon.network.response.XereonResponse
import com.xereon.xereon.update.UpdateChecker
import com.xereon.xereon.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface XereonAPI {

    companion object {
        const val ACCESS_KEY = BuildConfig.XEREON_ACCESS_KEY
        const val BASE_URL = "http://vordertuer.bplaced.net/"
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////   LOGIN / REGISTER   /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @GET("app-php/users/login.php")
    suspend fun login(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("firebasetoken") token: String
    ): Response<LoginResponse>

    @GET("app-php/users/create-user.php")
    suspend fun register(
        @Query("name") name: String,
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("firebasetoken") token: String
    ): Response<LoginResponse>

    @GET("app-php/users/reset-password.php")
    suspend fun resetPassword(@Query("email") emailAddress: String): Response<LoginResponse>

    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/home/app-config.php")
    suspend fun getMinVersion() : Response<UpdateChecker.Response>

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////   HOME / PRODUCTS / STORES   //////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/home/home.php")
    suspend fun getExploreData(
        @Query("id") userID: Int,
        @Query("postalcode") postalCode: String
    ): Response<ExploreData>

    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/stores/store-information.php")
    suspend fun getStore(
        @Query("id") storeID: Int
    ): Response<Store>

    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/products/products.php")
    suspend fun getProductsFromStore(
        @Query("id") storeID: Int,
        @Query("search") query: String,
        @Query("order") sort: Int = Constants.SortType.RESPONSE_NEW_FIRST.index,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<SimpleProduct>>

    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/products/product-information.php")
    suspend fun getProductDetails(
        @Query("id") productID: Int
    ): Response<Product>

    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/stores/get-stores-surroundings.php")
    suspend fun getStoresInArea(
        @Query("postalcode") zip: String
    ): Response<List<LocationStore>>


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////   SEARCH   /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @see CategoryUtils
     * @see Constants
     *
     * Example: Search for a store in category 0 (Lebensmittel) with the type "metzger" and the name
     * should include "heußler". The results should be ordered in type 1 (A->Z) and pagination is applyed
     *
     * main-search.php?name=heußler&postalcode=89542&category=0&type=metzger&order=1&page=1&limit=20
     *
     * If no category / name / type is given, the query is "" (empyt string)
     */
    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/debug/home/main-search.php")
    suspend fun searchStore(
        @Query("name") query: String? = null,
        @Query("postalcode") zip: String = Constants.DEFAULT_POSTCODE,
        @Query("category") category: Int? = null,
        @Query("type") type: String? = null,
        @Query("order") sort: Int = Constants.SortType.RESPONSE_NEW_FIRST.index,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
        ): Response<List<SimpleStore>>

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////   CHAT   ///////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/messages/get-chats.php")
    suspend fun getAllChats(
        @Query("user") userID: Int,
    ): Response<List<Chat>>

    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/messages/get-messages.php")
    suspend fun getChatMessages(
        @Query("user") userID: Int,
        @Query("store") storeID: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<List<ChatMessage>>

    @Headers("Authorization: $ACCESS_KEY")
    @GET("app-php/messages/send-message.php")
    suspend fun sendMessage(
        @Query("sender") userID: Int,
        @Query("receiver") storeID: Int,
        @Query("message") message: String = ""
    ): Response<XereonResponse>
}