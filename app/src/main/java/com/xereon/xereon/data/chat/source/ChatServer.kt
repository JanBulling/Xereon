package com.xereon.xereon.data.chat.source

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.xereon.xereon.R
import com.xereon.xereon.data.chat.Chat
import com.xereon.xereon.data.paging.ChatPagingSource
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.network.response.XereonResponse
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Resource
import dagger.Reusable
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

@Reusable
class ChatServer @Inject constructor(
    private val api: XereonAPI
){

    suspend fun getAllChats(userID: Int): Resource<List<Chat>> =
        try {
            val response = api.getAllChats(
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
            val response = api.sendMessage(
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
                    xereonAPI = api,
                    userID = userID,
                    storeID = storeID
                )
            }
        ).liveData

}