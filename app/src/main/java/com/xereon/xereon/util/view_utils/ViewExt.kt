package com.xereon.xereon.util.view_utils

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData

inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?) = true

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}

/*For updating a mutable live data on purpose*/
fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}