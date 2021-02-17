package com.xereon.xereon.ui.main.favorites

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.xereon.xereon.R
import com.xereon.xereon.data.favorites.source.FavoritesProvider
import com.xereon.xereon.data.store.FavoriteStore
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.viewmodel.XereonViewModel

class FavoritesFragmentViewModel @ViewModelInject constructor(
    private val favoritesProvider: FavoritesProvider
) : XereonViewModel() {

    val exceptions: SingleLiveEvent<Int> = SingleLiveEvent()
    val events: SingleLiveEvent<FavoritesEvents> = SingleLiveEvent()
    private val _sorting: MutableLiveData<Constants.SortType> = MutableLiveData()

    val favoriteStores = Transformations.switchMap(_sorting) {
        favoritesProvider.getFavorites(it)
    }.cachedInViewModel()

    fun sortFavorites(sortType: Constants.SortType) {
        _sorting.value = sortType
    }

    fun deleteFavorite(favorite: FavoriteStore) = launch {
        try {
            favoritesProvider.deleteFavorite(favorite)
            events.postValue(FavoritesEvents.UndoDeleteMessage(favorite))
        } catch (e: Exception) { exceptions.postValue(R.string.unexpected_exception) }
    }

    fun deleteAllFavorites() = launch {
        try {
            favoritesProvider.deleteAllFavorites()
        } catch (e: Exception) { exceptions.postValue(R.string.unexpected_exception) }
    }

    fun undoDelete(favorite: FavoriteStore) = launch {
        try {
            favoritesProvider.insertFavorite(favorite)
        } catch (e: Exception) { exceptions.postValue(R.string.unexpected_exception) }
    }

}