package com.xereon.xereon.ui.shoppingCart

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.xereon.xereon.db.OrderProductDao
import com.xereon.xereon.db.SimpleOrder
import com.xereon.xereon.db.StoreBasic
import com.xereon.xereon.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception

@ExperimentalCoroutinesApi
class ShoppingCartViewModel @ViewModelInject constructor(
    private val dao: OrderProductDao
) : ViewModel() {

    private val _stores: MutableLiveData<List<StoreBasic>> = MutableLiveData()
    val stores: LiveData<List<StoreBasic>> get() = _stores

    private val _totalPrice: MutableLiveData<Float> = MutableLiveData()
    val totalPrice: LiveData<Float> get() = _totalPrice

    private val _productsFromStore: MutableLiveData<List<SimpleOrder>> = MutableLiveData()
    val productsFromStore: LiveData<List<SimpleOrder>> get() = _productsFromStore

    fun getAllStores() {
        try {
            viewModelScope.launch {
                dao.getAllStores().collect {
                    _stores.value = it
                }
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in ShoppingCartViewModel: ${e.message}")
        }
    }

    fun getAllOrdersFromStore(storeId: Int) {
        try {
            viewModelScope.launch {
                dao.getAllOrdersFromStore(storeId).collect {
                    _productsFromStore.value = it
                }
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in ShoppingCartViewModel: ${e.message}")
        }
    }

    fun getTotalPrice() {
        try {
            viewModelScope.launch {
                dao.getTotalPrice().collect {
                    _totalPrice.value = it
                }
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in ShoppingCartViewModel: ${e.message}")
        }
    }
}