package com.xereon.xereon.util.cache

import android.util.Log
import com.google.gson.Gson
import dagger.Reusable
import java.lang.Exception
import javax.inject.Inject

@Reusable
class Parser @Inject constructor() {

    fun <T> parse(rawData: ByteArray, type: Class<T>): T? = try {
        val jsonString = String(rawData)
        Gson().fromJson(jsonString, type)
    } catch (e: Exception) {
        Log.e(TAG, e.message + " Error parsing the json.")
        null
    }

    fun <T> parse(data: T?, type: Class<T>) : ByteArray? = try {
        if (data == null)
            null
        else {
            val jsonString = Gson().toJson(data, type)
            jsonString.toByteArray()
        }
    } catch (e: Exception) {
        Log.e(TAG, e.message + " Error parsing the json.")
        null
    }

    companion object {
        const val TAG = "CACHE_PARSER"
    }

}