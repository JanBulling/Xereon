package com.xereon.xereon.ui.explore

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.xereon.xereon.data.model.ExploreData
import com.xereon.xereon.data.repository.ExploreRepository
import com.xereon.xereon.utils.ApplicationUtils
import com.xereon.xereon.utils.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ExploreViewModel
    @ViewModelInject
    constructor(
        private val exploreRepository : ExploreRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
    ) : ViewModel() {

    private val _dataState: MutableLiveData<DataState<ExploreData>> = MutableLiveData()

    val dataState : LiveData<DataState<ExploreData>>
        get() = _dataState

    fun getExploreData(userID: Int, zip: String, isRetry: Boolean = false) {
        if (!isRetry && _dataState.value != null)
            return

        viewModelScope.launch {
            exploreRepository.getExploreData(userID, zip).onEach { dataState ->
                _dataState.value = dataState
            }.launchIn(viewModelScope)
        }
    }
}
