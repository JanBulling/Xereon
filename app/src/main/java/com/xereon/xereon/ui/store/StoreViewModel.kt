package com.xereon.xereon.ui.store

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.data.repository.StoreRepository
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Constants.ORDER_DEFAULT
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.DataState
import com.xereon.xereon.util.DoubleTrigger
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class StoreViewModel @ViewModelInject constructor(
    private val storeRepository: StoreRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _storeId = savedStateHandle.getLiveData<Int>(STORE_ID_KEY)
    private val _currentQuery = savedStateHandle.getLiveData<String>(STORE_LAST_QUERY, "")
    private val _currentProductsOrder = savedStateHandle.getLiveData<Int>(STORE_LAST_PRODUCTS_ORDER, 0)

    private val _storeData: MutableLiveData<DataState<Store>> = MutableLiveData()


    val storeData : LiveData<DataState<Store>> get() = _storeData
    val currentQuery : String get() = _currentQuery.value ?: ""
    val currentProductOrder : Int get() = _currentProductsOrder.value ?: Constants.ORDER_DEFAULT

    val productData = Transformations.switchMap(DoubleTrigger(_currentQuery, _currentProductsOrder)) {
        Log.d(TAG, "${it.first},   ${it.second}")
        storeRepository.getProducts(_storeId.value ?: -1, it.first ?: "", it.second ?: ORDER_DEFAULT).cachedIn(viewModelScope)
    }


    fun getStoreData(storeId: Int, isRetry: Boolean = false) {
        if (!isRetry && _storeData.value != null)
            return
        viewModelScope.launch {
            _storeId.value = storeId
            storeRepository.getStoreData(storeId).onEach { dataState ->
                _storeData.value = dataState
            }.launchIn(viewModelScope)
        }
    }

    fun getAllProducts(storeId: Int, isRetry: Boolean = false) {
        if (productData.value == null || isRetry) {
            _storeId.value = storeId
            _currentQuery.value = ""
        }
    }

    fun searchProducts(query: String) { _currentQuery.value = query }
    fun sortProducts(order: Int) { _currentProductsOrder.value = order }

    companion object {
        private const val STORE_ID_KEY = "store_id"
        private const val STORE_LAST_QUERY = "last_query"
        private const val STORE_LAST_PRODUCTS_ORDER = "last_order"
    }
}