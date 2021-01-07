package com.xereon.xereon.ui.store

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.data.repository.StoreRepository
import com.xereon.xereon.util.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class StoreViewModel
    @ViewModelInject
    constructor(
        private val storeRepository: StoreRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
    ) : ViewModel() {

    private val _storeId = savedStateHandle.getLiveData<Int>(STORE_ID_KEY)
    private val _userId = savedStateHandle.getLiveData<Int>(USER_ID_KEY)
    private val _currentQuery = savedStateHandle.getLiveData<String>(STORE_LAST_QUERY, "")

    private val _storeData: MutableLiveData<DataState<Store>> = MutableLiveData()

    //getter
    val storeData : LiveData<DataState<Store>> get() = _storeData
    val currentQuery : LiveData<String> get() = _currentQuery
    val productData = _currentQuery.switchMap { query ->
        storeRepository.searchProduct(_storeId.value ?: 1, query).cachedIn(viewModelScope)
    }

    //getter methods
    fun getStoreData(isRetry: Boolean = false) {
        if (!isRetry && _storeData.value != null)
            return
        viewModelScope.launch {
            storeRepository.getStoreData(_storeId.value ?: 1).onEach { dataState ->
                _storeData.value = dataState
            }.launchIn(viewModelScope)
        }
    }

    fun getAllProducts(isRetry: Boolean = false) {
        if (productData.value == null || isRetry)
            _currentQuery.value = ""
    }

    fun searchProducts(query: String) { _currentQuery.value = query }

    fun setStoreId(id: Int) { _storeId.value = id }
    fun setUserId(id: Int) { _userId.value = id }

    companion object {
        private const val STORE_ID_KEY = "store_id"
        private const val USER_ID_KEY = "user_id"
        private const val STORE_LAST_QUERY = "last_query"
    }
}