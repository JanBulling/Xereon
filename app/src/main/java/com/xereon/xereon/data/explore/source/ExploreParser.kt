package com.xereon.xereon.data.explore.source

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.xereon.xereon.data.explore.ExploreData
import dagger.Reusable
import java.lang.Exception
import javax.inject.Inject

@Reusable
class ExploreParser @Inject constructor() {

    fun parse(rawData: ByteArray): ExploreData? = try {
        val jsonString = String(rawData)
        Gson().fromJson(jsonString, ExploreData::class.java)
    } catch (e: Exception) {
        Log.e(TAG, e.message + " Error parsing the json.")
        null
    }

    fun parse(exploreData: ExploreData) : ByteArray? = try {
        val jsonString = Gson().toJson(exploreData, ExploreData::class.java)
        jsonString.toByteArray()
    } catch (e: Exception) {
        Log.e(TAG, e.message + " Error parsing the json.")
        null
    }

    companion object {
        const val TAG = "EXPLORE_PARSER"
    }
}