package com.xereon.xereon.ui.products

sealed class ProductEvents {

    object NavigateBack : ProductEvents()
    data class NavigateStore(val storeId: Int, val storeName: String) : ProductEvents()
    data class NavigateProduct(val id: Int, val name: String) : ProductEvents()

}