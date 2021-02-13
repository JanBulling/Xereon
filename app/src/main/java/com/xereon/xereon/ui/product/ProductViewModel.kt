package com.xereon.xereon.ui.product

import android.util.Log
import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.xereon.xereon.R
import com.xereon.xereon.data.model.Product
import com.xereon.xereon.data.repository.ProductRepository
import com.xereon.xereon.data.util.PriceUtils
import com.xereon.xereon.db.OrderProductDao
import com.xereon.xereon.db.model.OrderProduct
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class ProductViewModel @ViewModelInject constructor(
    private val productRepository: ProductRepository,
    private val dao: OrderProductDao,
) : ViewModel() {

    sealed class ProductEvent {
        data class Success(val productData: Product) : ProductEvent()
        object Failure : ProductEvent()
        data class ShowErrorMessage(@StringRes val messageId: Int) : ProductEvent()
        object Loading : ProductEvent()
    }

    private val _productData = MutableLiveData<ProductEvent>(ProductEvent.Loading)
    val productData: LiveData<ProductEvent> get() = _productData

    private val _eventChannel = Channel<ProductEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun getProduct(productId: Int) {
        if (_productData.value is ProductEvent.Success)
            return
        viewModelScope.launch {
            try {
                _productData.value = ProductEvent.Loading
                when (val response = productRepository.getProduct(productId)) {
                    is Resource.Error -> {
                        _eventChannel.send(ProductEvent.ShowErrorMessage(response.message!!))
                        _productData.value = ProductEvent.Failure
                    }
                    is Resource.Success ->
                        _productData.value = ProductEvent.Success(response.data!!)
                }
            } catch (e: Exception) {
                Log.e(Constants.TAG, "Unexpected error in ProductViewModel: ${e.message}")
                _eventChannel.send(ProductEvent.ShowErrorMessage(R.string.unexpected_exception))
            }
        }
    }

    fun addToShoppingCart(count: Int) = viewModelScope.launch {
        try {
            _productData.value.let {
                if (it is ProductEvent.Success) {
                    val product = it.productData
                    insertInShoppingCartTable(product, count)
                }
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in ProductViewModel: ${e.message}")
            _eventChannel.send(ProductEvent.ShowErrorMessage(R.string.unexpected_exception))
        }
    }

    private suspend fun insertInShoppingCartTable(product: Product, count: Int) {
        val totalPrice =
            PriceUtils.calculateTotalPrice(product.price.toFloatOrNull(), product.unit, count)
        dao.insert(
            OrderProduct(
                id = product.id,
                name = product.name,
                price = product.price.toFloatOrNull() ?: 0f,
                totalPrice = totalPrice,
                unit = product.unit,
                count = count,
                storeID = product.storeID,
                storeName = product.storeName
            )
        )
    }
}