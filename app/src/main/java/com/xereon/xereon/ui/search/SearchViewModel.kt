package com.xereon.xereon.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xereon.xereon.data.repository.SearchRepository

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