package com.xereon.xereon.data.products.source

import android.util.Log
import com.xereon.xereon.R
import com.xereon.xereon.data.products.Product
import com.xereon.xereon.data.store.Store
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Resource
import dagger.Reusable
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

@Reusable
class ProductDataServer @Inject constructor(
    private val api: XereonAPI
) {

    suspend fun getProductData(productId: Int) : Resource<Product> = try {
        val response = api.getProductDetails(productID = productId)
        val result = response.body()
        if (response.isSuccessful && result != null)
            Resource.Success(result)
        else
            Resource.Error(R.string.unexpected_exception)

    } catch (e: Exception) {
        Log.e(Constants.TAG, "Error in Repository: ${e.stackTraceToString()}")
        when (e) {
            is HttpException -> Resource.Error(R.string.no_connection_exception)
            is IOException -> Resource.Error(R.string.no_connection_exception)
            else -> Resource.Error(R.string.unexpected_exception)
        }
    }

}