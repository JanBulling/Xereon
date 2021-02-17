package com.xereon.xereon.data.chat.source

import android.provider.SyncStateContract
import com.xereon.xereon.storage.LocalData
import com.xereon.xereon.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatProvider @Inject constructor(
    private val server: ChatServer,
    private val localData: LocalData,
) {
    suspend fun getAllChats() = server.getAllChats(localData.getUserID())

    suspend fun sendMessage(message: String, storeId: Int) = server.sendMessage(
        message = message,
        userID = localData.getUserID(),
        storeID = storeId)

    fun getChatMessages(storeId: Int) = server.getChatMessages(
        userID = localData.getUserID(),
        storeID = storeId
    )
}