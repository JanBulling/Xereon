package com.xereon.xereon.ui.product

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.xereon.xereon.data.model.Product
import com.xereon.xereon.data.repository.ProductRepository
import com.xereon.xereon.util.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProductViewModel @ViewModelInject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val _productData: MutableLiveData<DataState<Product>> = MutableLiveData()

    val productData : LiveData<DataState<Product>> get() = _productData

    fun getProductData(productId: Int, isRetry: Boolean = false) {
        if (!isRetry && _productData.value != null)
            return

        viewModelScope.launch {
            productRepository.getProductDetails(productId).onEach { dataState ->
                _productData.value = dataState
            }.launchIn(viewModelScope)
        }
    }

}