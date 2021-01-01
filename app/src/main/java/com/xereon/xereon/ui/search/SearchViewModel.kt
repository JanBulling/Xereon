package com.xereon.xereon.ui.search

import androidx.hilt.Assisted
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.xereon.xereon.data.repository.StoreRepository
import com.xereon.xereon.ui.store.StoreViewModel
import com.xereon.xereon.utils.ApplicationUtils

class SearchViewModel
    @androidx.hilt.lifecycle.ViewModelInject
    constructor(
        private val storeRepository: StoreRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _currentQuery = savedStateHandle.getLiveData<String>(STORE_LAST_QUERY, "")

    val productData = _currentQuery.switchMap { query ->
        val apiKey = ApplicationUtils.generateAPIkey(1)

        storeRepository.searchProduct(apiKey, 57, query)
    }

    fun searchProducts(query: String) { _currentQuery.value = query }


    companion object {
        private const val STORE_LAST_QUERY = "last_query"
    }
}