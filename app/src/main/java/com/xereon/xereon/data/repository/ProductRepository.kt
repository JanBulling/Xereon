package com.xereon.xereon.data.repository

import com.xereon.xereon.data.model.Product
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.DataState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(private val xereonAPI: XereonAPI) {

    suspend fun getProductDetails(productId: Int) : Flow<DataState<Product>> = flow {
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
    }

}