package com.xereon.xereon.ui.store

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.xereon.xereon.data.model.ExploreData
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.data.repository.ExploreRepository
import com.xereon.xereon.data.repository.StoreRepository
import com.xereon.xereon.utils.ApplicationUtils
import com.xereon.xereon.utils.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.reflect.Parameter
import kotlin.math.absoluteValue

class StoreViewModel
    @ViewModelInject
    constructor(
        private val storeRepository: StoreRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
    ) : ViewModel() {

    private val _storeId = savedStateHandle.getLiveData<Int>(STORE_ID_KEY)
    private val _userId = savedStateHandle.getLiveData<Int>(USER_ID_KEY)
    private val _storeData: MutableLiveData<DataState<Store>> = MutableLiveData()
    private val _currentQuery = savedStateHandle.getLiveData<String>(STORE_LAST_QUERY, "")


    //getter
    val storeData : LiveData<DataState<Store>> get() = _storeData
    val productData = _currentQuery.switchMap { query ->
        val apiKey = ApplicationUtils.generateAPIkey(_userId.value?: 1)

        storeRepository.searchProduct(apiKey, _storeId.value ?: 1, query)
    }


    //getter methods
    fun getStoreData(isRetry: Boolean = false) {
        if (!isRetry && _storeData.value != null)
            return
        val apiKey = ApplicationUtils.generateAPIkey(_userId.value?: 1)
        viewModelScope.launch {
            storeRepository.getStoreData(apiKey, _storeId.value ?: 1).onEach { dataState ->
                _storeData.value = dataState
            }.launchIn(viewModelScope)
        }
    }

    fun getAllProducts() { _currentQuery.value = "" }

    fun searchProducts(query: String) { _currentQuery.value = query }

    fun setStoreId(id: Int) { _storeId.value = id }
    fun setUserId(id: Int) { _userId.value = id }

    companion object {
        private const val STORE_ID_KEY = "store_id"
        private const val USER_ID_KEY = "user_id"
        private const val STORE_LAST_QUERY = "last_query"
    }
}