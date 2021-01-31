package com.xereon.xereon.data.repository

import android.util.Log
import com.xereon.xereon.R
import com.xereon.xereon.data.model.ExploreData
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.Resource
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExploreRepository @Inject constructor(private val xereonAPI: XereonAPI) {

    suspend fun getExploreData(userID: Int, zip: String): Resource<ExploreData> {
        return try {
            val response = xereonAPI.getExploreData(userID = userID, postalCode = zip, version = 1)
            val result = response.body()
            if (response.isSuccessful && result != null)
                Resource.Success(result)
            else
                Resource.Error(R.string.unexprected_exception)

        } catch (e: Exception) {
            Log.e(TAG, "Error in Repository: ${e.stackTraceToString()}")
            when (e) {
                is HttpException -> Resource.Error(R.string.no_connection_exception)
                is IOException -> Resource.Error(R.string.no_connection_exception)
                else -> Resource.Error(R.string.unexprected_exception)
            }
        }
    }

}