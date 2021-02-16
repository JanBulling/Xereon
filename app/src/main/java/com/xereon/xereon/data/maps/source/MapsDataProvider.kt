package com.xereon.xereon.data.maps.source

import android.util.Log
import com.xereon.xereon.data.maps.MapsData
import com.xereon.xereon.data.store.source.StoreDataServer
import com.xereon.xereon.di.ApplicationScope
import com.xereon.xereon.storage.LocalData
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.cache.Parser
import com.xereon.xereon.util.flow.HotDataFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapsDataProvider @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val server: MapsServer,
    private val storeServer: StoreDataServer,
    private val cache: MapsCache,
    private val parser: Parser,
    private val localData: LocalData,
) {

    private val mapsCameraPosition = HotDataFlow(
        loggingTag = TAG,
        scope = scope,
        sharingBehavior = SharingStarted.WhileSubscribed(
            stopTimeoutMillis = 5000,   /*5 sec*/
            replayExpirationMillis = 0
        )
    ) {
        fromCache() ?: fromPreferences()
    }

    init {
        mapsCameraPosition.updateSafely { fromPreferences() }
    }

    val currentMapsPosition: Flow<MapsData> = mapsCameraPosition.data


    private fun fromCache(): MapsData? = try {
        Log.d(TAG, "fromCache()")
        cache.load()?.let { parser.parse(it, MapsData::class.java) }
    } catch (e: Exception) {
        Log.w(TAG, "Failed to parse cached data.")
        null
    }

    private fun fromPreferences(): MapsData = try {
        Log.d(TAG, "fromPreferences()")
        val coordinates = localData.getLatLng()
        val zoom = Constants.DEFAULT_ZOOM
        val postCode = localData.getPostCode()
        MapsData(
            lat = coordinates.latitude.toFloat(),
            lng = coordinates.longitude.toFloat(),
            zoom = zoom,
            postCode = postCode
        )
    } catch (e: Exception) {
        Log.w(TAG, "Failed to parse preference data.")
        MapsData(
            lat = Constants.DEFAULT_LAT,
            lng = Constants.DEFAULT_LNG,
            zoom = Constants.DEFAULT_ZOOM,
            postCode = Constants.DEFAULT_POSTCODE
        )
    }

    fun saveToCache(mapsData: MapsData) {
        mapsCameraPosition.updateSafely {
            try {
                parser.parse(mapsData, MapsData::class.java).let { cache.save(it) }
                mapsData
            } catch (e: Exception) {
                Log.w(TAG, "Failed to safe maps data.")
                this@updateSafely
            }
        }
    }

    suspend fun getStoreData(storeId: Int) = storeServer.getStoreData(storeId)

    suspend fun getStoresInRegion(postCode: String) =
        server.getStoresInRegion(postCode)

    companion object {
        private const val TAG = "MAPS_DATA_PROVIDER"
    }
}