package com.xereon.xereon.ui.stores.products

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xereon.xereon.data.repository.StoreRepository
import com.xereon.xereon.data.store.source.StoreDataProvider
import com.xereon.xereon.ui.stores.StoreEvents
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.viewmodel.XereonViewModel

class StoreProductsFragmentViewModel @ViewModelInject constructor(
    private val storeDataProvider: StoreDataProvider
) : XereonViewModel() {

    data class SearchQuery(
        val storeId: Int = -1,
        val query: String = "",
        val sorting: Constants.SortType = Constants.SortType.RESPONSE_NEW_FIRST,
    )

    fun onBackClick() { events.postValue(StoreEvents.NavigateBack) }

    val events: SingleLiveEvent<StoreEvents> = SingleLiveEvent()

    private val _search: MutableLiveData<SearchQuery> = MutableLiveData()
    val search: SearchQuery get() = _search.value ?: SearchQuery()

    val products = Transformations.switchMap(_search) {
        storeDataProvider.getProducts(it.storeId, it.query, it.sorting)
    }.cachedInViewModel()

    fun getAllProducts(storeId: Int) {
        if (products.value != null)
            return

        _search.value = SearchQuery(storeId = storeId)
    }
    fun searchProducts(query: String) { _search.value = _search.value?.copy(query = query) }
    fun orderProducts(sorting: Constants.SortType) {
        _search.value = _search.value?.copy(sorting = sorting)
    }

    fun onProductClick(id: Int, name: String) { events.postValue(StoreEvents.NavigateToProduct(id, name)) }
}