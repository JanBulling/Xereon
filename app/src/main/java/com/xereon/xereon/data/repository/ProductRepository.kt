package com.xereon.xereon.data.repository

import com.xereon.xereon.data.model.Product
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Resource
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(private val xereonAPI: XereonAPI) {

    /*suspend fun getProductDetails(productId: Int) : Flow<DataState<Product>> = flow {
        emit(DataState.Loading)
        try {
            val networkStoreData = xereonAPI.getProductDetails(productId)

            emit(DataState.Success(networkStoreData))
        } catch (e : Exception) {
            when (e) {
                is IOException, is HttpException -> emit(DataState.Error("Keine Verbindung"))
                else -> emit(DataState.Error("Es ist ein Fehler unterlaufen"))
            }
        }
    }*/

    suspend fun getProduct(productId: Int): Resource<Product> {
        return try {
            val response = xereonAPI.getProductDetails(productID = productId)
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