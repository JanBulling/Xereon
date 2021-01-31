package com.xereon.xereon.ui.explore

import android.util.Log.e
import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.xereon.xereon.R
import com.xereon.xereon.data.model.ExploreData
import com.xereon.xereon.data.repository.ExploreRepository
import com.xereon.xereon.db.OrderProductDao
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class ExploreViewModel @ViewModelInject constructor(
    private val exploreRepository: ExploreRepository,
    private val dao: OrderProductDao,
) : ViewModel() {

    sealed class ExploreEvent {
        data class Success(val exploreData: ExploreData) : ExploreEvent()
        object Failure : ExploreEvent()
        data class ShowErrorMessage(@StringRes val errorMessageId: Int) : ExploreEvent()
        object Loading : ExploreEvent()
    }

    private val _exploreData: MutableLiveData<ExploreEvent> = MutableLiveData(ExploreEvent.Loading)
    val exploreData: LiveData<ExploreEvent> get() = _exploreData

    private val _ordersCount = MutableLiveData<Int>()
    val ordersCount: LiveData<Int> = _ordersCount

    private val _eventChannel = Channel<ExploreEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    init {
        getOrdersCount()
    }

    fun getExploreData(userId: Int, postcode: String) {
        if (_exploreData.value is ExploreEvent.Success)
            return
        viewModelScope.launch {
            try {
                _exploreData.value = ExploreEvent.Loading
                when (val response = exploreRepository.getExploreData(userID = userId, zip = postcode)) {
                    is Resource.Error -> {
                        _eventChannel.send(ExploreEvent.ShowErrorMessage(response.message!!))
                        _exploreData.value = ExploreEvent.Failure
                    }
                    is Resource.Success -> _exploreData.value =
                        ExploreEvent.Success(response.data!!)
                }
            } catch (e: Exception) {
                e(TAG, "Unexpected error in ExploreViewModel: ${e.message}")
                _eventChannel.send(ExploreEvent.ShowErrorMessage(R.string.unexprected_exception))
            }
        }
    }

    private fun getOrdersCount() = viewModelScope.launch {
        try {
            dao.getNumberOrders().collect {
                _ordersCount.value = it
            }
        } catch (e: Exception) {
            e(TAG, "Unexpected error in ExploreViewModel: ${e.message}")
            _eventChannel.send(ExploreEvent.ShowErrorMessage(R.string.unexprected_exception))
            _ordersCount.value = 0
        }
    }
}
