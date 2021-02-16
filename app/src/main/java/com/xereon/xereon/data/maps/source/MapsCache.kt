package com.xereon.xereon.data.maps.source

import android.util.Log
import com.xereon.xereon.data.maps.MapsCache
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapsCache @Inject constructor(
    @MapsCache cacheDir: File,
) {

    private val cacheFile = File(cacheDir, "cache_raw")

    fun load(): ByteArray? = try {
        if (cacheFile.exists()) cacheFile.readBytes() else null
    } catch (e: Exception) {
        Log.e(TAG, e.message + " Failed to load raw data from cache.")
        null
    }

    fun save(data: ByteArray?) {
        if (data == null) {
            if (cacheFile.exists() && cacheFile.delete()) {
                Log.d(TAG, "Cache file was deleted.")
            }
            return
        }
        if (cacheFile.exists()) {
            Log.d(TAG, "Overwriting with new data (size=${data.size})")
        }
        cacheFile.parentFile?.mkdirs()
        cacheFile.writeBytes(data)
    }

    companion object {
        const val TAG = "MAPS_CACHE"
    }

}