package com.xereon.xereon.ui.categories

import android.os.Parcelable
import android.util.Log
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
        class Failure(val errorText: String): CategoryEvent()
        object Loading : CategoryEvent()
    }

    @Parcelize
    data class SearchQuery(
        val query: String = "",
        val type: String = "---",
        val zip: String = Constants.DEFAULT_ZIP
    ) : Parcelable

    private val _search = savedStateHandle.getLiveData<SearchQuery>(CATEGORY_QUERY)
    val searchQuery: String get() = _search.value?.query ?: ""

    private val _exampleStores: MutableLiveData<CategoryEvent> = MutableLiveData(CategoryEvent.Loading)
    val exampleStores: LiveData<CategoryEvent> get() = _exampleStores

    val typeStores = Transformations.switchMap(_search) {
        repository.getStoresByType(it.type, it.query, it.zip).cachedIn(viewModelScope)
    }

    //private val _exampleStores: MutableLiveData<DataState<List<SimpleStore>>> = MutableLiveData()


    //val exampleStores: LiveData<DataState<List<SimpleStore>>> get() = _exampleStores


    /*fun getExampleStoresWithCategory(category: Int, zip: String, isRetry: Boolean = false) {
        if (_categoryId == category && !isRetry)
            return

        viewModelScope.launch(dispatchers.io) {
            repository.getExampleStoresForCategory(category, zip).onEach { dataState ->
                _exampleStores.value = dataState
                _categoryId = category
            }.launchIn(viewModelScope)
        }
    }*/

    fun searchStore(searchQuery: SearchQuery) { _search.value = searchQuery }

    fun getExampleStores(category: Int) {
        try {
            if (_exampleStores.value is CategoryEvent.Success)
                return
            viewModelScope.launch {
                _exampleStores.value = CategoryEvent.Loading
                when (val response = repository.getExampleStoresForCategory(category = category, zip = "89542")) {
                    is Resource.Error -> _exampleStores.value = CategoryEvent.Failure(response.message!!)
                    is Resource.Success -> _exampleStores.value = CategoryEvent.Success(response.data!!)
                }
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in ExploreViewModel: ${e.message}")
        }
    }

    companion object {
        private const val CATEGORY_QUERY = "keys.ui.category.categoryViewModel.query"
    }
}
