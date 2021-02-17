package com.xereon.xereon.ui.chat

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xereon.xereon.data.chat.Chat
import com.xereon.xereon.data.chat.source.ChatProvider
import com.xereon.xereon.storage.LocalData
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Resource
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.viewmodel.XereonViewModel

class ChatOverviewFragmentViewModel @ViewModelInject constructor(
    private val chatProvider: ChatProvider,
    private val localData: LocalData
) : XereonViewModel() {

    private val _chats: MutableLiveData<List<Chat>> = MutableLiveData()
    val chats: LiveData<List<Chat>> get() = _chats

    val exceptions: SingleLiveEvent<Int> = SingleLiveEvent()
    val events: SingleLiveEvent<ChatEvents> = SingleLiveEvent()

    fun getAllChats() = launch {
        if (localData.isLocationSet() && (localData.isAccountValid() || localData.getUserID() != Constants.DEFAULT_USER_ID)) {
            when (val response = chatProvider.getAllChats()) {
                is Resource.Success -> _chats.postValue(response.data)
                is Resource.Error -> exceptions.postValue(response.message)
            }
        } else {
            events.postValue(ChatEvents.ShowNoAccountDialog)
        }
    }

}
