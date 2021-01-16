package com.xereon.xereon.ui.search

import android.provider.MediaStore
import android.util.Log.d
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.xereon.xereon.data.model.ExploreData
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.data.model.places.Place
import com.xereon.xereon.data.repository.PlacesRepository
import com.xereon.xereon.data.repository.SearchRepository
import com.xereon.xereon.network.PlacesRequest
import com.xereon.xereon.network.PlacesResponse
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Constants.DEFAULT_ZIP
import com.xereon.xereon.util.Constants.ORDER_DEFAULT
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.DataState
import com.xereon.xereon.util.QuadTrigger
import com.xereon.xereon.util.TripleTrigger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.ArrayList

class SearchViewModel @ViewModelInject constructor(
    private val repository: SearchRepository
) : ViewModel() {
    val query = MutableLiveData("")
    var selectedCategory: Int = -1
    var selectedZip = "89542"
    var selectedOrdering = ORDER_DEFAULT
    var expandedRegion = true

    private val searchTrigger = MutableLiveData<Int>()

    fun newSearch() {
        searchTrigger.value = searchTrigger.value?.plus(1) ?: 0
    }

    val stores = searchTrigger.switchMap {
        repository.searchStore(
            query = query.value ?: "",
            category = selectedCategory,
            zip = selectedZip,
            orderStores = selectedOrdering
        ).cachedIn(viewModelScope)
    }


    fun onQueryChanged(query: String) {
        this.query.value = query
    }

    fun onSelectedCategoryChanged(category: Int) {
        this.selectedCategory = category
    }

    fun onSelectedZipChanged(zip: String) {
        this.selectedZip = zip
    }

    fun onSelectedOrderingChanged(storeOrder: Int) {
        this.selectedOrdering = storeOrder
    }

    fun onExpandedSettingChanged(useExpanded: Boolean) {
        this.expandedRegion = useExpanded
    }
}