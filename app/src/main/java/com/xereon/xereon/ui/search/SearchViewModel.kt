package com.xereon.xereon.ui.search

import android.os.Parcelable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.google.android.gms.maps.model.LatLng
import com.xereon.xereon.data.repository.SearchRepository
import com.xereon.xereon.di.ProvLatLng
import com.xereon.xereon.di.ProvPostCode
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Constants.SortTypes
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

class SearchViewModel @ViewModelInject constructor(
    private val repository: SearchRepository,
    @Assisted private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    @JvmField @Inject @ProvPostCode var postcode: String = Constants.DEFAULT_POSTCODE

    @Parcelize
    data class SearchQuery(
        val query: String = "",
        val category: Int = -1,
        val postcode: String = Constants.DEFAULT_POSTCODE,
        val sorting: SortTypes = SortTypes.SORT_RESPONSE_NEW_FIRST,
    ) : Parcelable

    private val _query = savedStateHandle.getLiveData<SearchQuery>(SEARCH_QUERY)
    val query: LiveData<SearchQuery> get() = _query

    var queryText: String = ""
    var category: Int = -1
    var sorting: SortTypes = SortTypes.SORT_RESPONSE_NEW_FIRST

    val searchResponse = Transformations.switchMap(_query) {
        repository.searchStore(
            query = _query.value?.query ?: "",
            category = _query.value?.category ?: -1,
            zip = _query.value?.postcode ?: postcode,
            sort = _query.value?.sorting ?: SortTypes.SORT_RESPONSE_NEW_FIRST
        ).cachedIn(viewModelScope)
    }

    fun newSearch() {
        val searchQuery = SearchQuery(
            query = queryText,
            category = category,
            postcode = postcode,
            sorting = sorting
        )
        _query.value = searchQuery
    }

    companion object {
        private const val SEARCH_QUERY = "keys.ui.search.searchViewModel.query"
    }
}