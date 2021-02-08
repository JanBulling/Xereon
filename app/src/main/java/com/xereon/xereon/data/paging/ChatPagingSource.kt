package com.xereon.xereon.data.paging

import androidx.paging.PagingSource
import com.xereon.xereon.data.model.ChatMessage
import com.xereon.xereon.network.XereonAPI

class ChatPagingSource(
    private val xereonAPI: XereonAPI,
    private val userID: Int,
    private val storeID: Int,
) : PagingSource<Int, ChatMessage>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChatMessage> {
        val currentPage = params.key ?: 1

        return try {
            val response = xereonAPI.getChatMessages(
                userID = userID,
                storeID = storeID,
                page = currentPage,
                limit = params.loadSize
            )
            val result = response.body()
            if (!response.isSuccessful || result == null)
                return LoadResult.Error(Throwable(response.message()))

            LoadResult.Page(
                data = result,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (result.size < params.loadSize) null else currentPage + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}