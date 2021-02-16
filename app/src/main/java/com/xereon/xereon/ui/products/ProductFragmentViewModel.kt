package com.xereon.xereon.ui.products

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.xereon.xereon.data.products.Product
import com.xereon.xereon.data.products.source.ProductDataProvider
import com.xereon.xereon.ui.products.items.ProductAddToCart
import com.xereon.xereon.ui.products.items.ProductBasicInformation
import com.xereon.xereon.ui.products.items.ProductItem
import com.xereon.xereon.util.Resource
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.viewmodel.XereonViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class ProductFragmentViewModel @ViewModelInject constructor(
    private val productDataProvider: ProductDataProvider
) : XereonViewModel() {

    val events: SingleLiveEvent<ProductEvents> = SingleLiveEvent()
    val exceptions: SingleLiveEvent<Int> = SingleLiveEvent()

    private val _productsData: MutableStateFlow<Product?> = MutableStateFlow(null)

    val productItems: LiveData<List<ProductItem>> = _productsData.map {
        if (it == null)
            return@map emptyList<ProductItem>()

        mutableListOf<ProductItem>().apply {
            add(ProductBasicInformation.Item(it))
            add(ProductAddToCart.Item(it.price, it.unit) {count ->

            })
        }

    }.asLiveData()

    fun getProductData(productId: Int) {
        if (_productsData.value != null)
            return
        launch {
            when (val response = productDataProvider.getProductData(productId)) {
                is Resource.Success -> _productsData.value = response.data!!
                is Resource.Error -> exceptions.postValue(response.message!!)
            }

        }
    }

    fun onBackClick() { events.postValue(ProductEvents.NavigateBack) }

}