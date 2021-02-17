package com.xereon.xereon.ui.main.favorites

import com.xereon.xereon.data.store.FavoriteStore

sealed class FavoritesEvents {

    data class UndoDeleteMessage(val favorite: FavoriteStore) : FavoritesEvents()

}