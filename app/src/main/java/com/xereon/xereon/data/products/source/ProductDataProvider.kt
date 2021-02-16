package com.xereon.xereon.data.products.source

import com.xereon.xereon.data.products.Product
import com.xereon.xereon.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductDataProvider @Inject constructor(
    private val server: ProductDataServer
) {

    suspend fun getProductData(productId: Int): Resource<Product> = server.getProductData(productId)

}