package com.xereon.xereon.ui.shoppingCart

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.room.FtsOptions
import com.xereon.xereon.data.util.PriceUtils
import com.xereon.xereon.db.OrderProductDao
import com.xereon.xereon.db.StoreBasic
import com.xereon.xereon.db.model.OrderProduct
import com.xereon.xereon.util.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class ShoppingCartViewModel @ViewModelInject constructor(
    private val dao: OrderProductDao
) : ViewModel() {

    sealed class ShoppingCartEvent {
        data class ShowUndoDeleteMessage(val order: OrderProduct) : ShoppingCartEvent()
    }

    private val _stores: MutableLiveData<List<StoreBasic>> = MutableLiveData()
    val stores: LiveData<List<StoreBasic>> get() = _stores

    private val _store: MutableLiveData<StoreBasic> = MutableLiveData()
    val store: LiveData<StoreBasic> get() = _store

    private val _totalPrice: MutableLiveData<Float> = MutableLiveData()
    val totalPrice: LiveData<Float> get() = _totalPrice

    private val _productsFromStore: MutableLiveData<List<OrderProduct>> = MutableLiveData()
    val productsFromStore: LiveData<List<OrderProduct>> get() = _productsFromStore

    private val _eventChannel = Channel<ShoppingCartEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()


    fun getAllStores() = viewModelScope.launch {
        try {
            dao.getAllStores().collect {
                _stores.value = it
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in ShoppingCartViewModel: ${e.message}")
        }
    }

    fun getStore(storeId: Int) = viewModelScope.launch {
        try {
            dao.getStoreById(storeId).collect {
                _store.value = it
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in ShoppingCartViewModel: ${e.message}")
        }
    }

    fun getAllOrdersFromStore(storeId: Int) {
        if (_productsFromStore.value != null)
            return

        viewModelScope.launch {
            try {
                dao.getAllOrdersFromStore(storeId).collect {
                    _productsFromStore.value = it
                }
            } catch (e: Exception) {
                Log.e(Constants.TAG, "Unexpected error in ShoppingCartViewModel: ${e.message}")
            }
        }
    }

    fun getTotalPrice() = viewModelScope.launch {
        try {
            dao.getTotalPrice().collect {
                _totalPrice.value = it
            }
        } catch (e: Exception) {
            _totalPrice.value = 0f
            Log.e(Constants.TAG, "Unexpected error in ShoppingCartViewModel: ${e.message}")
        }
    }

    fun increaseOrderCount(order: OrderProduct) = viewModelScope.launch {
        try {
            if (PriceUtils.getMaxCount(order.unit) > order.count) {
                val newCount = order.count + 1
                val copy = order.copy(count = newCount, totalPrice = PriceUtils.calculateTotalPrice(order.price, order.unit, newCount))
                dao.updateOrder(copy)
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in ShoppingCartViewModel: ${e.message}")
        }
    }

    fun decreaseOrderCount(order: OrderProduct) = viewModelScope.launch {
        try {
            if (order.count > 1) {
                val newCount = order.count - 1
                val copy = order.copy(count = newCount, totalPrice = PriceUtils.calculateTotalPrice(order.price, order.unit, newCount))
                dao.updateOrder(copy)
            } else {
                dao.deleteOrder(order)
                _eventChannel.send(ShoppingCartEvent.ShowUndoDeleteMessage(order))
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in ShoppingCartViewModel: ${e.message}")
        }
    }

    fun deleteOrder(order: OrderProduct) = viewModelScope.launch {
        try {
            dao.deleteOrder(order)
            _eventChannel.send(ShoppingCartEvent.ShowUndoDeleteMessage(order))
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in ShoppingCartViewModel: ${e.message}")
        }
    }

    fun deleteAll(storeId: Int) = viewModelScope.launch {
        try {
            dao.deleteAllProductsFromStore(storeId)
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in ShoppingCartViewModel: ${e.message}")
        }
    }

    fun undoDeleteOrder(order: OrderProduct) = viewModelScope.launch {
        try {
            dao.insert(order)
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in ShoppingCartViewModel: ${e.message}")
        }
    }
}