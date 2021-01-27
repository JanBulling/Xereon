package com.xereon.xereon.ui.store

import android.os.Parcelable
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.xereon.xereon.BuildConfig
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.data.repository.StoreRepository
import com.xereon.xereon.ui.categories.CategoryViewModel
import com.xereon.xereon.ui.product.ProductViewModel
import com.xereon.xereon.ui.search.SearchViewModel
import com.xereon.xereon.util.*
import com.xereon.xereon.util.Constants.SortTypes
import com.xereon.xereon.util.Constants.TAG
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class StoreViewModel @ViewModelInject constructor(
    private val storeRepository: StoreRepository,
    @Assisted private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    sealed class StoreEvent {
        data class Success(val storeData: Store) : StoreEvent()
        data class Failure(val errorText: String) : StoreEvent()
        data class ShowErrorMessage(val message: String) : StoreEvent()
        object Loading : StoreEvent()
    }

    @Parcelize
    data class SearchQuery(
        val storeId: Int = -1,
        val query: String = "",
        val sorting: SortTypes = SortTypes.SORT_RESPONSE_NEW_FIRST,
    ) : Parcelable

    private val _search = savedStateHandle.getLiveData<SearchQuery>(STORE_SEARCH_PRODUCT_QUERY)

    private val _storeData = MutableLiveData<StoreEvent>(StoreEvent.Loading)
    val storeData: LiveData<StoreEvent> get() = _storeData

    private val _eventChannel = Channel<StoreEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    val products = Transformations.switchMap(_search) {
        storeRepository.getProducts(it.storeId, it.query, it.sorting).cachedIn(viewModelScope)
    }

    var queryText: String = ""
    var storeId: Int = -1
    var sorting: SortTypes = SortTypes.SORT_RESPONSE_NEW_FIRST

    fun getStore(storeId: Int) {
        if (_storeData.value is StoreEvent.Success)
            return
        viewModelScope.launch {
            try {
                _storeData.value = StoreEvent.Loading
                when (val response = storeRepository.getStore(storeId)) {
                    is Resource.Error -> {
                        if (BuildConfig.DEBUG)
                            _eventChannel.send(StoreEvent.ShowErrorMessage(response.message!!))
                        _storeData.value = StoreEvent.Failure(response.message!!)
                    }
                    is Resource.Success -> _storeData.value = StoreEvent.Success(response.data!!)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error in StoreViewModel: ${e.message}")
                _eventChannel.send(StoreEvent.ShowErrorMessage("Ein unerwarteter Fehler ist aufgetreten"))
            }
        }
    }

    private fun newProductSearch() {
        val searchQuery = SearchQuery(
            storeId = storeId,
            query = queryText,
            sorting = sorting
        )
        _search.value = searchQuery
    }

    fun getAllProducts(storeId: Int) {
        if (products.value != null)
            return

        this.queryText = ""
        this.storeId = storeId
        newProductSearch()
    }

    fun sortProduct(sorting: SortTypes) {
        this.sorting = sorting
        newProductSearch()
    }

    fun searchProduct(textQuery: String) {
        this.queryText = textQuery
        newProductSearch()
    }

    companion object {
        private const val STORE_SEARCH_PRODUCT_QUERY = "keys.ui.store.storeViewModel.query"
    }
}