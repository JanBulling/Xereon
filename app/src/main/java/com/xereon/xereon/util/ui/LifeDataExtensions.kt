package com.xereon.xereon.util.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeLiveData(fragment: Fragment, callback: (T) -> Unit) {
    observe(fragment.viewLifecycleOwner, Observer { callback.invoke(it) })
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner? = null, onValueChanged: (t: T) -> Unit) {
    val internalObserver = object : Observer<T> {
        override fun onChanged(t: T) {
            onValueChanged(t)
            removeObserver(this)
        }
    }
    if (lifecycleOwner == null) {
        observeForever(internalObserver)
    } else {
        observe(lifecycleOwner, internalObserver)
    }
}

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}