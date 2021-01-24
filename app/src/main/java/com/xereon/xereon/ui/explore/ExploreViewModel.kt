package com.xereon.xereon.ui.explore

import android.util.Log.e
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.xereon.xereon.data.model.ExploreData
import com.xereon.xereon.data.repository.ExploreRepository
import com.xereon.xereon.db.OrderProductDao
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception

class ExploreViewModel @ViewModelInject constructor(
        private val exploreRepository : ExploreRepository,
        private val dao: OrderProductDao,
) : ViewModel() {

    sealed class ExploreEvent {
        class Success(val exploreData: ExploreData): ExploreEvent()
        class Failure(val errorText: String): ExploreEvent()
        object Loading : ExploreEvent()
    }

    private val _exploreData: MutableLiveData<ExploreEvent> = MutableLiveData(ExploreEvent.Loading)
    val exploreData: LiveData<ExploreEvent> get() = _exploreData

    private val _ordersCount = MutableLiveData<Int>()
    val ordersCount: LiveData<Int> = _ordersCount

    init {
        getExploreData()
        getOrdersCount()
    }

    fun getExploreData() {
        try {
            if (_exploreData.value is ExploreEvent.Success)
                return
            viewModelScope.launch {
                _exploreData.value = ExploreEvent.Loading
                when (val response = exploreRepository.getExploreData(1, "89547")) {
                    is Resource.Error -> _exploreData.value = ExploreEvent.Failure(response.message!!)
                    is Resource.Success -> _exploreData.value = ExploreEvent.Success(response.data!!)
                }
            }
        } catch (e: Exception) {
            e(TAG, "Unexpected error in ExploreViewModel: ${e.message}")
        }
    }

    private fun getOrdersCount() {
        try {
            viewModelScope.launch {
                dao.getNumberOrders().collect {
                    _ordersCount.value = it
                }
            }
        } catch (e: Exception) {
            e(TAG, "Unexpected error in ExploreViewModel: ${e.message}")
        }
    }
}
