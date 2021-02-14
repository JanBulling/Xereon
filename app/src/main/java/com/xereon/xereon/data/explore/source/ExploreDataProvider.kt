package com.xereon.xereon.data.explore.source

import android.util.Log
import com.xereon.xereon.R
import com.xereon.xereon.data.explore.ExploreData
import com.xereon.xereon.di.ApplicationScope
import com.xereon.xereon.di.InjectPostCode
import com.xereon.xereon.di.InjectUserId
import com.xereon.xereon.storage.LocalData
import com.xereon.xereon.util.Resource
import com.xereon.xereon.util.cache.Parser
import com.xereon.xereon.util.device.ForegroundState
import com.xereon.xereon.util.flow.HotDataFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExploreDataProvider @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val server: ExploreDataServer,
    private val cache: ExploreCache,
    private val parser: Parser,
    //foregroundState: ForegroundState,
    private val  localData: LocalData,
) {

    private val exploreData = HotDataFlow(
        loggingTag = TAG,
        scope = scope,
        sharingBehavior = SharingStarted.WhileSubscribed(
            stopTimeoutMillis = 5000,   /*5 sec*/
            replayExpirationMillis = 0
        )
    ) {
        try {
            fromCache() ?: fromServer(localData.getUserID(), localData.getPostCode())
        } catch (e: Exception) {
            Log.e(TAG, e.message + "Failed to get data.")
            Resource.Error<ExploreData>(R.string.unexpected_exception)
        }
    }

    init {
        Log.d(TAG, "Initiation of ExploreDataProvider")

        triggerUpdate()

        /*foregroundState.isInForeground.onEach{
                if (it) {
                    Log.d(TAG, "App moved to foreground triggering explore update.")
                    triggerUpdate()
                }
            }
            .catch { Log.e(TAG, "Failed to trigger explore update.") }
            .launchIn(scope)*/
    }

    val current: Flow<Resource<ExploreData>> = exploreData.data

    private fun triggerUpdate() {
        Log.d(TAG,"triggerUpdate()")
        exploreData.updateSafely {
            try {
                fromServer(localData.getUserID(), localData.getPostCode())
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update explore data.")
                this@updateSafely // return previous data
            }
        }
    }

    private fun fromCache() : Resource<ExploreData>? = try {
        Log.d(TAG,"fromCache()")
        cache.load()?.let { parser.parse(it, ExploreData::class.java)?.let { Resource.Success(it) }}
    } catch (e: Exception) {
        Log.w(TAG, "Failed to parse cached data.")
        null
    }

    private suspend fun fromServer(userId: Int, postCode: String): Resource<ExploreData> {
        Log.d(TAG,"fromServer()")
        return server.getExploreData(userId, postCode).also {
            if (it is Resource.Success) parser.parse(it.data!!, ExploreData::class.java)?.let { cache.save( it ) }
        }
    }

    companion object {
        private const val TAG = "EXPLORE_DATA_PROV"
    }
}