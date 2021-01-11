package com.xereon.xereon.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.xereon.xereon.data.model.ExploreData
import com.xereon.xereon.data.repository.PlacesRepository
import com.xereon.xereon.data.repository.SearchRepository
import com.xereon.xereon.network.PlacesRequest
import com.xereon.xereon.network.PlacesResponse
import com.xereon.xereon.util.DataState
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SearchViewModel @ViewModelInject constructor(
    private val repository: SearchRepository
) : ViewModel() {

    private val currentQuery = MutableLiveData<String>()
    val stores = currentQuery.switchMap { query ->
        repository.searchStoreByName(query, "89542").cachedIn(viewModelScope)
    }

    fun searchStoresByName(nameQuery: String) {
        currentQuery.value = nameQuery
    }

}