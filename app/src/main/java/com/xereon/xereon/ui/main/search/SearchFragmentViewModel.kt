package com.xereon.xereon.ui.main.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.xereon.xereon.data.search.source.SearchProvider
import com.xereon.xereon.storage.LocalData
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.viewmodel.XereonViewModel

class SearchFragmentViewModel @ViewModelInject constructor(
    private val localData: LocalData,
    private val searchProvider: SearchProvider,
) : XereonViewModel() {

    data class SearchQuery(
        val query: String = "",
        val category: Int = -1,
        val postcode: String = Constants.DEFAULT_POSTCODE,
        val sorting: Constants.SortType = Constants.SortType.RESPONSE_NEW_FIRST,
    )

    val events: SingleLiveEvent<SearchEvents> = SingleLiveEvent()

    private val _query: MutableLiveData<SearchQuery> = MutableLiveData()
    val query: SearchQuery get() = _query.value ?: SearchQuery()

    private var _searchQuery = SearchQuery()

    val stores = Transformations.switchMap(_query) {
        searchProvider.search(it.query, it.category, it.sorting)
    }.cachedInViewModel()


    fun search(query: String) { _searchQuery = _searchQuery.copy(query = query) }
    fun search(sorting: Constants.SortType) { _searchQuery = _searchQuery.copy(sorting = sorting) }
    fun search(category: Int) { _searchQuery = _searchQuery.copy(category = category) }

    fun performSearch() {
        _query.value = _searchQuery
    }

    fun onStoreClick(storeId: Int, storeName: String) { events.postValue(SearchEvents.NavigateToStore(storeId, storeName)) }
    fun onSortClick() { events.postValue(SearchEvents.OpenSortMenu) }
}