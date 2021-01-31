package com.xereon.xereon.ui.categories

import android.os.Parcelable
import android.util.Log
import androidx.annotation.StringRes
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.data.repository.CategoryRepository
import com.xereon.xereon.util.*
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.launch
import java.lang.Exception

class CategoryViewModel @ViewModelInject constructor(
    private val repository: CategoryRepository,
    @Assisted private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    sealed class CategoryEvent {
        class Success(val examplesData: List<SimpleStore>) : CategoryEvent()
        object Failure : CategoryEvent()
        object Loading : CategoryEvent()
    }

    @Parcelize
    data class SearchQuery(
        val query: String = "",
        val type: String = "---",
        val postcode: String = Constants.DEFAULT_POSTCODE
    ) : Parcelable

    private val _search = savedStateHandle.getLiveData<SearchQuery>(CATEGORY_QUERY)
    val searchQuery: String get() = _search.value?.query ?: ""

    private val _exampleStores: MutableLiveData<CategoryEvent> =
        MutableLiveData(CategoryEvent.Loading)
    val exampleStores: LiveData<CategoryEvent> get() = _exampleStores

    val typeStores = Transformations.switchMap(_search) {
        repository.getStoresByType(it.type, it.query, it.postcode).cachedIn(viewModelScope)
    }

    fun searchStore(searchQuery: SearchQuery) {
        _search.value = searchQuery
    }

    fun getExampleStores(category: Int, postcode: String) {
        if (_exampleStores.value is CategoryEvent.Success)
            return
        viewModelScope.launch {
            try {
                _exampleStores.value = CategoryEvent.Loading
                when (val response =
                    repository.getExampleStoresForCategory(category = category, zip = postcode)) {
                    is Resource.Error ->
                        _exampleStores.value = CategoryEvent.Failure
                    is Resource.Success ->
                        _exampleStores.value = CategoryEvent.Success(response.data!!)
                }
            } catch (e: Exception) {
                Log.e(Constants.TAG, "Unexpected error in ExploreViewModel: ${e.message}")
            }
        }
    }

    companion object {
        private const val CATEGORY_QUERY = "keys.ui.category.categoryViewModel.query"
    }
}
