package com.xereon.xereon.ui.chat

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import com.xereon.xereon.data.chat.ChatMessage
import com.xereon.xereon.data.chat.source.ChatProvider
import com.xereon.xereon.storage.LocalData
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.viewmodel.XereonViewModel

class ChatFragmentViewModel @ViewModelInject constructor(
    private val chatProvider: ChatProvider,
    private val localData: LocalData
) : XereonViewModel() {

    val events: SingleLiveEvent<ChatEvents> = SingleLiveEvent()
    private val _storeId = MutableLiveData<Int>()

    val chatMessages = Transformations.switchMap(_storeId) {
        if (localData.isLocationSet() && (localData.isAccountValid() || localData.getUserID() != Constants.DEFAULT_USER_ID))
            chatProvider.getChatMessages(it)
        else {
            events.postValue(ChatEvents.ShowNoAccountDialog)
            MutableLiveData(PagingData.empty())
        }
    }.cachedInViewModel()

    fun sendMessage(message: String, storeId: Int) = launch {
        chatProvider.sendMessage(message, storeId)
        _storeId.value = storeId
    }

    fun getMessages(storeId: Int) {
        _storeId.value = storeId
    }
}