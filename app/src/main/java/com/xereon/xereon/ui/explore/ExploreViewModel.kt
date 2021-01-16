package com.xereon.xereon.ui.explore

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.xereon.xereon.data.model.ExploreData
import com.xereon.xereon.data.repository.ExploreRepository
import com.xereon.xereon.util.DataState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ExploreViewModel @ViewModelInject constructor(
        private val exploreRepository : ExploreRepository
) : ViewModel() {

    val exploreData= MutableLiveData<DataState<ExploreData>>()
    val loading = MutableLiveData(false)
    val error = MutableLiveData<String>()

    init {
        newExploreData()
    }

    fun newExploreData() {
        viewModelScope.launch {
            exploreRepository.getExploreData(1, "89542").onEach { dataState ->
                exploreData.value = dataState
            }.launchIn(viewModelScope)

        }
    }
}
