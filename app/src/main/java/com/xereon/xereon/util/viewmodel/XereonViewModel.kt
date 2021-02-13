package com.xereon.xereon.util.viewmodel

import android.util.Log
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.coroutines.CoroutineContext

abstract class XereonViewModel constructor(
    private val childViewModels: List<XereonViewModel> = emptyList()
) : ViewModel() {

    private val TAG: String = this::class.simpleName!!

    fun launch(
        block: suspend CoroutineScope.() -> Unit
    ) {
        try {
            viewModelScope.launch(block = block)
        } catch (e: CancellationException) {
            Log.w(TAG, e.message + "launch()ed coroutine was canceled.")
        }
    }

    fun <T> Flow<T>.launchInViewModel() = this.launchIn(viewModelScope)

    @CallSuper
    override fun onCleared() {
        Log.v(TAG, "onCleared()")
        childViewModels.forEach {
            Log.v(TAG, "Clearing child VM: $it")
            it.onCleared()
        }
        super.onCleared()
    }

}