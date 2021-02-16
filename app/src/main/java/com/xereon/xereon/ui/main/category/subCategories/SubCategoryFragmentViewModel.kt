package com.xereon.xereon.ui.main.category.subCategories

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import androidx.paging.PagingData
import com.xereon.xereon.data.search.source.SearchProvider
import com.xereon.xereon.data.store.SimpleStore
import com.xereon.xereon.ui.stores.products.StoreProductsFragmentViewModel
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.viewmodel.XereonViewModel

class SubCategoryFragmentViewModel @ViewModelInject constructor(
    private val searchProvider: SearchProvider,
) : XereonViewModel() {

    val events: SingleLiveEvent<SubCategoryEvents> = SingleLiveEvent()

    data class SearchQuery(
        val query: String = "",
        val type: String = "",
    )

    private val _query: MutableLiveData<SearchQuery> = MutableLiveData()
    val query get() = _query.value ?: SearchQuery()

    val stores = Transformations.switchMap(_query){
        searchProvider.search(it.query, -1, it.type, Constants.SortType.RESPONSE_NEW_FIRST)
    }.cachedInViewModel()

    fun getStores(subCategory: String) {
        if (stores.value != null)
            return

        _query.value = SearchQuery(type = subCategory)
    }
    fun searchStore(query: String) { _query.value = _query.value?.copy(query = query) }

    fun onBackClick() {
        events.postValue(SubCategoryEvents.NavigateBack)
    }
}