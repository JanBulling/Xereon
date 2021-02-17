package com.xereon.xereon.ui.chat

import androidx.annotation.StringRes
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.xereon.xereon.R
import com.xereon.xereon.data.chat.Chat
import com.xereon.xereon.data.repository.ChatRepository
import com.xereon.xereon.util.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
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

    sealed class ChatOverviewEvent {
        data class Error(@StringRes val messageId: Int) : ChatOverviewEvent()
        data class Success(val data: List<Chat>) : ChatOverviewEvent()
    }

    private val chatMessageMetaData = stateHandle.getLiveData(CHAT_META_DATA, Pair(-1, -1))

    private val _dataState: MutableLiveData<ChatEvent> = MutableLiveData()
    val dataState: LiveData<ChatEvent> get() = _dataState

    private val _chatsData: MutableLiveData<ChatOverviewEvent> = MutableLiveData()
    val chatsData: LiveData<ChatOverviewEvent> get() = _chatsData

    val chatMessages = Transformations.switchMap(chatMessageMetaData) {
        repository.getChatMessages(it.first, it.second).cachedIn(viewModelScope)
    }

    private val _eventChannel = Channel<ChatOverviewEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun getAllChats(userId: Int) = viewModelScope.launch {
        try {
            when(val response = repository.getAllChats(userID = userId)) {
                is Resource.Success -> {
                    _chatsData.value = ChatOverviewEvent.Success(response.data!!)
                }
                is Resource.Error -> {
                    _eventChannel.send(ChatOverviewEvent.Error(response.message!!))
                    _chatsData.value = ChatOverviewEvent.Error(response.message!!)
                }
            }
        } catch (e: Exception) {
            _eventChannel.send(ChatOverviewEvent.Error(R.string.unexpected_exception))
        }
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
            _dataState.value = ChatEvent.SendError
            //_eventChannel.send(ChatEvent.Error(R.string.unexprected_exception))
        }
    }

    companion object {
        private const val CHAT_META_DATA = "keys.ui.chat.ChatViewModel.chat_meta_data"
    }
}