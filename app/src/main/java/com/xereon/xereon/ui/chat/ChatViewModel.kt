package com.xereon.xereon.ui.chat

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.xereon.xereon.data.repository.ChatRepository
import com.xereon.xereon.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class ChatViewModel @ViewModelInject constructor(
    private val repository: ChatRepository,
    @Assisted private val stateHandle: SavedStateHandle,
) : ViewModel() {

    sealed class ChatEvent{
        object Load : ChatEvent()
        object SendSuccess : ChatEvent()
        object SendError : ChatEvent()
    }

    private val chatMessageMetaData = stateHandle.getLiveData(CHAT_META_DATA, Pair(-1, -1))

    private val _dataState: MutableLiveData<ChatEvent> = MutableLiveData()
    val dataState: LiveData<ChatEvent> get() = _dataState

    val chatMessages = Transformations.switchMap(chatMessageMetaData) {
        repository.getChatMessages(it.first, it.second).cachedIn(viewModelScope)
    }

    fun getChatMessages(userId: Int, storeId: Int) {
        if (userId != chatMessageMetaData.value?.first ?: -1 &&
                storeId != chatMessageMetaData.value?.second ?: -1) {
            stateHandle.set(CHAT_META_DATA, Pair(userId, storeId))
            //chatMessageMetaData.value = Pair(userId, storeId)
        }
    }

    fun sendMessage(message: String, userId: Int, storeId: Int) = viewModelScope.launch {
        try {

            _dataState.value = ChatEvent.Load
            when (repository.sendMessage(message, userId, storeId)) {
                is Resource.Success ->
                    _dataState.value = ChatEvent.SendSuccess
                is Resource.Error ->
                    _dataState.value = ChatEvent.SendError
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val CHAT_META_DATA = "keys.ui.chat.ChatViewModel.chat_meta_data"
    }
}