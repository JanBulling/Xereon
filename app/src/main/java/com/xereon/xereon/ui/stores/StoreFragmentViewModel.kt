package com.xereon.xereon.ui.stores

import android.text.Html
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.xereon.xereon.data.store.Store
import com.xereon.xereon.data.model.places.GooglePlacesData
import com.xereon.xereon.data.store.StoreIdentificationData
import com.xereon.xereon.data.store.source.StoreDataProvider
import com.xereon.xereon.ui.stores.items.*
import com.xereon.xereon.util.Resource
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.viewmodel.XereonViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class StoreFragmentViewModel @ViewModelInject constructor(
    private val storeDataProvider: StoreDataProvider,
) : XereonViewModel() {

    val events: SingleLiveEvent<StoreEvents> = SingleLiveEvent()
    val exceptions: SingleLiveEvent<Int> = SingleLiveEvent()
    val storeName: MutableLiveData<String> = MutableLiveData()

    private val _storeData: MutableStateFlow<Store?> = MutableStateFlow(null)

    val storeItems: LiveData<List<StoreItem>> = _storeData.map {
        if (it == null)
            return@map emptyList<StoreItem>()

        storeName.postValue(Html.fromHtml(it.name).toString())

        mutableListOf<StoreItem>().apply {
            add(StoreBasicInformation.Item(it,
                { events.postValue(StoreEvents.OpenNavigation(it.latLng)) },
                { Log.d(TAG, "clicked on favorite") },
                { events.postValue(StoreEvents.NavigateChat(StoreIdentificationData(it.id, it.name))) }))

            add(StoreOpeningHour.Item(it.openinghours))

            add(StorePeakTimes.Item(GooglePlacesData(
                currentPopularity = 50,
                popularTimes = listOf(
                    arrayOf(0,0,0,0,0,0, 20, 32, 45, 65, 70, 60, 50, 50, 65, 80, 82, 62, 40, 15,  5, 0,0,0),
                    arrayOf(0,0,0,0,0,0, 15, 28, 40, 60, 65, 65, 62, 68, 72, 75, 68, 40, 30, 15,  8, 0,0,0),
                    arrayOf(0,0,0,0,0,0, 15, 28, 40, 55, 62, 62, 65, 72, 78, 82, 65, 55, 30, 18,  8, 0,0,0),
                    arrayOf(0,0,0,0,0,0, 12, 24, 55, 72, 85, 77, 72, 68, 72, 77, 72, 55, 30, 12,  8, 0,0,0),
                    arrayOf(0,0,0,0,0,0, 20, 35, 52, 62, 68, 65, 62, 65, 72, 78, 75, 58, 45, 19, 11, 0,0,0),
                    arrayOf(0,0,0,0,0,0, 15, 32, 57, 80, 95, 98, 95, 90, 93, 98, 95, 78, 52, 26, 12, 0,0,0),
                    arrayOf(0,0,0,0,0,0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0,0,0)
                )
            )))

            add(StoreProductsRedirect.Item(it.exampleProducts,
                {
                    events.postValue(StoreEvents.NavigateToProducts(it.id, it.name))
                }, {
                    events.postValue(StoreEvents.NavigateToProduct(it.id, it.name))
                }))
        }
    }.asLiveData()

    fun getStoreData(storeId: Int) {
        if (_storeData.value != null)
            return
        launch {
            when (val response = storeDataProvider.getStoreData(storeId)) {
                is Resource.Success -> _storeData.value = response.data!!
                is Resource.Error -> exceptions.postValue(response.message!!)
            }

        }
    }

    fun reportStore(storeId: Int) {
        events.postValue(StoreEvents.StoreReported)
    }

    fun onBackClick() { events.postValue(StoreEvents.NavigateBack) }

    companion object {
        private const val TAG = "STORE_VIEW_MODEL"
    }
}