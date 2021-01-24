package com.xereon.xereon.ui.search

import android.os.Parcelable
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.data.repository.SearchRepository
import com.xereon.xereon.ui.categories.CategoryViewModel
import com.xereon.xereon.ui.explore.ExploreViewModel
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Constants.SortTypes
import com.xereon.xereon.util.Constants.TAG
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchViewModel @ViewModelInject constructor(
    private val repository: SearchRepository,
    @Assisted private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    @Parcelize
    data class SearchQuery(
        val query: String = "",
        val category: Int = -1,
        val zip: String = Constants.DEFAULT_ZIP,
        val sorting: SortTypes = SortTypes.SORT_RESPONSE_DEFAULT,
    ) : Parcelable

    private val _query = savedStateHandle.getLiveData<SearchQuery>(SEARCH_QUERY)
    val query: LiveData<SearchQuery> get() = _query

    var queryText: String = ""
    var category: Int = -1
    var zip: String = Constants.DEFAULT_ZIP
    var sorting: SortTypes = SortTypes.SORT_RESPONSE_DEFAULT

    val searchResponse = Transformations.switchMap(_query) {
        repository.searchStore(
            query = _query.value?.query ?: "",
            category = _query.value?.category ?: -1,
            zip = _query.value?.zip ?: Constants.DEFAULT_ZIP,
            sort = _query.value?.sorting ?: SortTypes.SORT_RESPONSE_DEFAULT
        ).cachedIn(viewModelScope)
    }

    fun newSearch() {
        val searchQuery = SearchQuery(
            query = queryText,
            category = category,
            zip = zip,
            sorting = sorting
        )
        _query.value = searchQuery
    }

    companion object {
        private const val SEARCH_QUERY = "keys.ui.search.searchViewModel.query"
    }
}