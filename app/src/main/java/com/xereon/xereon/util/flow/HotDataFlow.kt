package com.xereon.xereon.util.flow

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

class HotDataFlow<T : Any> (
    loggingTag: String,
    scope: CoroutineScope,
    sharingBehavior: SharingStarted = SharingStarted.WhileSubscribed(),
    forwardException: Boolean = true,
    private val startValueProvider: suspend CoroutineScope.() -> T
) {
    private val tag = "$loggingTag:HD"

    private val updateActions = MutableSharedFlow<suspend (T) -> T>(
        replay = Int.MAX_VALUE,
        extraBufferCapacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    private val valueGuard = Mutex()

    private val internalProducer: Flow<Holder<T>> = channelFlow {
        var currentValue = valueGuard.withLock {
            startValueProvider().also {
                Log.v(tag, "startValue=$it")
                val updatedBy: suspend T.() -> T = { it }
                send(Holder.Data(value = it, updatedBy = updatedBy))
            }
        }
        Log.v(tag, "startValue=$currentValue")

        updateActions
            .onCompletion {
                Log.v(tag, "updateActions onCompletion -> resetReplayCache()")
                updateActions.resetReplayCache()
            }
            .collect { updateAction ->
                currentValue = valueGuard.withLock {
                    updateAction(currentValue).also {
                        send(Holder.Data(value = it, updatedBy = updateAction))
                    }
                }
            }

        Log.v(tag, "internal channelFlow finished.")
    }

    private val internalFlow = internalProducer
        .onStart { Log.v(tag, "Internal onStart") }
        .catch {
            if (forwardException) {
                Log.w(tag, it.message + "Forwarding internal Error")
                // Wrap the error to get it past `sharedIn`
                emit(Holder.Error(error = it))
            } else {
                Log.e(tag, it.message + "Throwing internal Error")
                throw it
            }
        }
        .onCompletion { err ->
            if (err != null) Log.w(tag, err.message + "internal onCompletion due to error")
            else Log.v(tag,"internal onCompletion")
        }
        .shareIn(
            scope = scope,
            replay = 1,
            started = sharingBehavior
        )
        .map {
            when (it) {
                is Holder.Data<T> -> it
                is Holder.Error<T> -> throw it.error
            }
        }

    val data: Flow<T> = internalFlow.map { it.value }.distinctUntilChanged()

    fun updateSafely(update: suspend T.() -> T) = updateActions.tryEmit(update)

    suspend fun updateBlocking(update: suspend T.() -> T): T {
        updateActions.tryEmit(update)
        Log.v(tag,"Waiting for update.")
        return internalFlow.first { it.updatedBy == update }.value
    }

    internal sealed class Holder<T> {
        data class Data<T>(
            val value: T,
            val updatedBy: suspend T.() -> T
        ) : Holder<T>()

        data class Error<T>(
            val error: Throwable
        ) : Holder<T>()
    }
}