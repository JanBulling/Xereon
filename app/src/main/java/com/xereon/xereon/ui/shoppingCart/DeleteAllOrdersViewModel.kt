package com.xereon.xereon.ui.shoppingCart

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.xereon.xereon.storage.OrderProductDao
import com.xereon.xereon.util.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllOrdersViewModel @ViewModelInject constructor(
    private val dao: OrderProductDao,
    @ApplicationScope private val applicationScope: CoroutineScope,
): ViewModel() {

    fun onConfirmClick(storeId: Int) = applicationScope.launch {
        dao.deleteAllProductsFromStore(storeId)
    }
}