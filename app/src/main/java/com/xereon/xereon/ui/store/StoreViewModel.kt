package com.xereon.xereon.ui.store

import android.os.Parcelable
import android.util.Log
import androidx.annotation.StringRes
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.xereon.xereon.BuildConfig
import com.xereon.xereon.R
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.data.repository.StoreRepository
import com.xereon.xereon.db.FavoriteStoreDao
import com.xereon.xereon.db.model.FavoriteStore
import com.xereon.xereon.util.*
import com.xereon.xereon.util.Constants.SortType
import com.xereon.xereon.util.Constants.TAG
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class StoreViewModel @ViewModelInject constructor(
    private val storeRepository: StoreRepository,
    private val dao: FavoriteStoreDao,
    @Assisted private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    sealed class StoreEvent {
        data class Success(val storeData: Store) : StoreEvent()
        object Failure : StoreEvent()
        data class ShowErrorMessage(@StringRes val messageId: Int) : StoreEvent()
        object ShowAddedFavorites : StoreEvent()
        object Loading : StoreEvent()
    }

    @Parcelize
    data class SearchQuery(
        val storeId: Int = -1,
        val query: String = "",
        val sorting: SortType = SortType.RESPONSE_NEW_FIRST,
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
    var sorting: SortType = SortType.RESPONSE_NEW_FIRST

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
                        _storeData.value = StoreEvent.Failure
                    }
                    is Resource.Success -> _storeData.value = StoreEvent.Success(response.data!!)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error in StoreViewModel: ${e.message}")
                _eventChannel.send(StoreEvent.ShowErrorMessage(R.string.unexpected_exception))
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

    fun sortProduct(sorting: SortType) {
        this.sorting = sorting
        newProductSearch()
    }

    fun searchProduct(textQuery: String) {
        this.queryText = textQuery
        newProductSearch()
    }

    fun addStoreToFavorites() = viewModelScope.launch {
        val storeEvent = _storeData.value
        if (storeEvent is StoreEvent.Success) {
            val store = storeEvent.storeData
            val favorite = FavoriteStore(
                id = store.id,
                name = store.name,
                city = store.city,
                type = store.type,
                 category = store.category,
                insertDate = System.currentTimeMillis()
            )
            dao.insert(favorite)
            _eventChannel.send(StoreEvent.ShowAddedFavorites)
        }
    }

    companion object {
        private const val STORE_SEARCH_PRODUCT_QUERY = "keys.ui.store.storeViewModel.query"
    }
}