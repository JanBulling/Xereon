package com.xereon.xereon.ui.map

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.xereon.xereon.data.model.LocationStore
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.data.repository.StoreRepository
import com.xereon.xereon.util.Constants.MAX_NUMBER_STORES_ON_MAP
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MapViewModel @ViewModelInject constructor(
    private val storeRepository: StoreRepository,
    @Assisted stateHandle: SavedStateHandle
) : ViewModel() {

    private var cameraPosition = CameraPosition.fromLatLngZoom(LatLng(48.6231869016793, 10.166387513676847), 13f)

    private val loadedZips: MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf())
    private val _storeData: MutableLiveData<MutableList<LocationStore>> = MutableLiveData(mutableListOf())
    private val _dataState: MutableLiveData<DataState<*>> = MutableLiveData()

    private val _currentStoreData: MutableLiveData<DataState<Store>> = MutableLiveData()

    //getter
    val currentStoreData : LiveData<DataState<Store>> get() = _currentStoreData

    val dataState : LiveData<DataState<*>>
        get() = _dataState
    val storeData : LiveData<MutableList<LocationStore>>
        get() = _storeData

    fun saveCurrentMapPosition(cameraPosition: CameraPosition?) {
        if (cameraPosition != null)
            this.cameraPosition = cameraPosition
    }

    fun getMapPosition() = cameraPosition

    fun getStoreData(storeId: Int) {
        viewModelScope.launch {
            storeRepository.getStoreData(storeId).onEach { dataState ->
                _currentStoreData.value = dataState
            }.launchIn(viewModelScope)
        }
    }

    fun getStoresInArea(zip: String, isRetry: Boolean = false, initialCall: Boolean = false) {
        if (loadedZips.value!!.size > 0 && initialCall) {
            _storeData.value!!.apply {
                _storeData.postValue(this)
            }
            return
        }

        if (!isRetry && loadedZips.value!!.contains(zip.substring(0, 4))) {
            Log.d(TAG, "already loaded zip code")
            return
        }

        viewModelScope.launch {

            loadedZips.value!!.apply {
                add(zip.substring(0, 4))
                loadedZips.postValue(this)
            }

            storeRepository.getStoresInArea(zip).onEach { data ->
                _dataState.value = data

                if (data is DataState.Success) {
                    _storeData.value!!.apply {
                        if (size > MAX_NUMBER_STORES_ON_MAP) { //if more stores are loaded, clear all and also clear loaded postal codes
                            clear()
                            loadedZips.value!!.apply {
                                clear()
                                add(zip.substring(0, 4))
                                loadedZips.postValue(this)
                            }
                        }
                        addAll(data.data)
                        _storeData.postValue(this)
                    }
                } else if (data is DataState.Error) {
                    loadedZips.value!!.apply {
                        remove(zip.substring(0, 4))
                        loadedZips.postValue(this)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}