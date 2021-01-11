package com.xereon.xereon.ui.categories

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.data.repository.CategoryRepository
import com.xereon.xereon.util.DataState
import com.xereon.xereon.util.DoubleTrigger
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CategoryViewModel @ViewModelInject constructor(
    private val repository: CategoryRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _categoryId: Int = -1
    private val _categoryType = savedStateHandle.getLiveData<String>(CATEGORY_TYPE_KEY)
    private val _currentQuery = savedStateHandle.getLiveData<String>(CATEGORY_LAST_QUERY, "")

    private val _exampleStores: MutableLiveData<DataState<List<SimpleStore>>> = MutableLiveData()


    val exampleStores: LiveData<DataState<List<SimpleStore>>> get() = _exampleStores
    val currentQuery : String get() = _currentQuery.value ?: ""

    val storeData = Transformations.switchMap(DoubleTrigger(_categoryType, _currentQuery)) {
        repository.getStoresByType(it.first ?: "---", it.second ?: "", "89542").cachedIn(viewModelScope)
    }

    fun getExampleStoresWithCategory(category: Int, zip: String, isRetry: Boolean = false) {
        if (_categoryId == category && !isRetry)
            return

        viewModelScope.launch {
            repository.getExampleStoresForCategory(category, zip).onEach { dataState ->
                _exampleStores.value = dataState
                _categoryId = category
            }.launchIn(viewModelScope)
        }
    }

    fun getAllStoresWithType(type: String, isRetry: Boolean = false) {
        if (storeData.value == null || isRetry) {
            _categoryType.value = type
        }
    }
    fun searchStore(query: String) { _currentQuery.value = query }


    companion object {
        private const val CATEGORY_TYPE_KEY = "category_type"
        private const val CATEGORY_LAST_QUERY = "category_query"
    }
}
