package com.xereon.xereon.data.repository

import android.util.Log
import com.xereon.xereon.R
import com.xereon.xereon.data.products.Product
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.Resource
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(private val xereonAPI: XereonAPI) {

    suspend fun getProduct(productId: Int): Resource<Product> {
        return try {
            val response = xereonAPI.getProductDetails(productID = productId)
            val result = response.body()
            if (response.isSuccessful && result != null)
                Resource.Success(result)
            else
                Resource.Error(R.string.unexpected_exception)

        } catch (e: Exception) {
            Log.e(TAG, "Error in Repository: ${e.stackTraceToString()}")
            when (e) {
                is HttpException -> Resource.Error(R.string.no_connection_exception)
                is IOException -> Resource.Error(R.string.no_connection_exception)
                else -> Resource.Error(R.string.unexpected_exception)
            }
        }
    }

}