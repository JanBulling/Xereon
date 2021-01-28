package com.xereon.xereon.ui.favorites

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.xereon.xereon.data.repository.FavoriteRepository
import com.xereon.xereon.data.repository.StoreRepository
import com.xereon.xereon.db.FavoriteStoreDao
import com.xereon.xereon.db.model.FavoriteStore
import com.xereon.xereon.db.model.OrderProduct
import com.xereon.xereon.ui.shoppingCart.ShoppingCartViewModel
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Constants.SortTypes
import com.xereon.xereon.util.Constants.TAG
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class FavoritesViewModel @ViewModelInject constructor(
    private val repository: FavoriteRepository,
    private val dao: FavoriteStoreDao,
    @Assisted stateHandle: SavedStateHandle,
) : ViewModel() {

    sealed class FavoritesEvent {
        data class ShowUndoDeleteMessage(val favorite: FavoriteStore) : FavoritesEvent()
    }

    private val _sorting = stateHandle.getLiveData<SortTypes>(SORTING_ORDER, SortTypes.SORT_RESPONSE_NEW_FIRST)

    val favorites = Transformations.switchMap(_sorting) {
        repository.getFavorites(it)
    }.cachedIn(viewModelScope)

    private val _eventChannel = Channel<FavoritesEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun sortElements(sortType: SortTypes) { _sorting.value = sortType }

    fun deleteFavorite(favorite: FavoriteStore) = viewModelScope.launch {
        try {
            dao.deleteFavorite(favorite)
            _eventChannel.send(FavoritesEvent.ShowUndoDeleteMessage(favorite))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in ShoppingCartViewModel: ${e.message}")
        }
    }

    fun deleteAll() = viewModelScope.launch {
        try {
            dao.deleteAllFavorites()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in ShoppingCartViewModel: ${e.message}")
        }
    }

    fun undoDelete(favorite: FavoriteStore) = viewModelScope.launch {
        try {
            dao.insert(favorite)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in ShoppingCartViewModel: ${e.message}")
        }
    }

    companion object {
        private const val SORTING_ORDER = "keys.ui.favorites.favoritesViewModel.sorting"
    }
}