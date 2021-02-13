package com.xereon.xereon.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.xereon.xereon.R
import com.xereon.xereon.data.model.Chat
import com.xereon.xereon.data.paging.ChatPagingSource
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.network.response.XereonResponse
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Resource
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val xereonAPI: XereonAPI
) {

    suspend fun getAllChats(userID: Int): Resource<List<Chat>> =
        try {
            val response = xereonAPI.getAllChats(
                userID = userID
            )
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

    suspend fun sendMessage(message: String, userID: Int, storeID: Int): Resource<XereonResponse> {
        return try {
            val response = xereonAPI.sendMessage(
                message = message,
                userID = userID,
                storeID = storeID
            )
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


    fun getChatMessages(userID: Int, storeID: Int,) =
        Pager(
            config = PagingConfig(
                initialLoadSize = 40,
                pageSize = 40,
                maxSize = 400,
                prefetchDistance = 10,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                ChatPagingSource(
                    xereonAPI = xereonAPI,
                    userID = userID,
                    storeID = storeID
                )
            }
        ).liveData
}