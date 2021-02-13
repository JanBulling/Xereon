package com.xereon.xereon.util.device

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.xereon.xereon.di.ProcessLifecycle
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForegroundState @Inject constructor(
    @ProcessLifecycle val processLifecycleOwner: LifecycleOwner
) {

    val isInForeground: Flow<Boolean> by lazy {
        MutableStateFlow(false).apply {
            val foregroundStateUpdater = object : LifecycleObserver {
                @Suppress("unused")
                @OnLifecycleEvent(Lifecycle.Event.ON_START)
                fun onAppForegrounded() {
                    Log.v(TAG, "App is in the foreground")
                    tryEmit(true)
                }

                @Suppress("unused")
                @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
                fun onAppBackgrounded() {
                    Log.v(TAG, "App is in the background")
                    tryEmit(false)
                }
            }

            val processLifecycle = processLifecycleOwner.lifecycle
            processLifecycle.addObserver(foregroundStateUpdater)
        }
            .onStart { Log.v(TAG, "isInForeground FLOW start") }
            .onEach { Log.v(TAG, "isInForeground FLOW emission: $it") }
            .onCompletion { Log.v(TAG, "isInForeground FLOW completed.") }
    }

    companion object {
        private const val TAG = "ForegroundState"
    }
}