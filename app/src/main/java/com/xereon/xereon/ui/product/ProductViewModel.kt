package com.xereon.xereon.ui.product

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.xereon.xereon.data.model.Product
import com.xereon.xereon.data.repository.ProductRepository
import com.xereon.xereon.data.util.PriceUtils
import com.xereon.xereon.db.OrderProductDao
import com.xereon.xereon.db.model.OrderProduct
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class ProductViewModel @ViewModelInject constructor(
    private val productRepository: ProductRepository,
    private val dao: OrderProductDao,
) : ViewModel() {

    sealed class ProductEvent {
        class Success(val productData: Product): ProductEvent()
        class Failure(val errorText: String): ProductEvent()
        object Loading : ProductEvent()
    }

    private val _productData = MutableLiveData<ProductEvent>(ProductEvent.Loading)
    val productData: LiveData<ProductEvent> get() = _productData


    fun getProduct(productId: Int) {
        try {
            if (_productData.value is ProductEvent.Success)
                return
            viewModelScope.launch {
                _productData.value = ProductEvent.Loading
                when (val response = productRepository.getProduct(productId)) {
                    is Resource.Error -> _productData.value = ProductEvent.Failure(response.message!!)
                    is Resource.Success -> _productData.value = ProductEvent.Success(response.data!!)
                }
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in ProductViewModel: ${e.message}")
        }
    }

    fun addToShoppingCart(count: Int) {
        try {
            viewModelScope.launch {
                _productData.value.let {
                    if (it is ProductEvent.Success) {
                        val product = it.productData
                        insertInShoppingCartTable(product, count)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in ProductViewModel: ${e.message}")
        }
    }

    private suspend fun insertInShoppingCartTable(product: Product, count: Int) {
        val totalPrice = PriceUtils.calculateTotalPrice(product.price.toFloatOrNull(), product.unit, count)
        dao.insert(
            OrderProduct(
                id = product.id,
                name = product.name,
                price = product.price.toFloatOrNull() ?: 0f,
                completePrice = totalPrice,
                unit = product.unit,
                count = count,
                storeID = product.storeID,
                storeName = product.storeName
            )
        )
    }
}