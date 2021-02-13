package com.xereon.xereon.data.explore.source

import android.util.Log
import com.xereon.xereon.R
import com.xereon.xereon.data.explore.ExploreData
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Resource
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class ExploreDataServer @Inject constructor(
    private val api: XereonAPI
) {

    suspend fun getExploreData(userId: Int, postCode: String) : Resource<ExploreData> = try {
            val response = api.getExploreData(userID = userId, postalCode = postCode)
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

    companion object{
        private const val TAG = "EXPLORE_SERVER"
    }
}