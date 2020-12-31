package com.xereon.xereon.data.repository

import android.util.Log
import com.xereon.xereon.data.model.ExploreData
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExploreRepository @Inject constructor(private val xereonAPI: XereonAPI) {

    suspend fun getExploreData(API_KEY: String, userID: Int, zip : String): Flow<DataState<ExploreData>> = flow {
        emit(DataState.Loading)
        try {
            val networkExplore = xereonAPI.getExploreData(API_KEY, userID, zip, 1)
            Log.d("[APP_DEBUG]", "getExploreData(): finished loading")
            /*
            Here implement caching.
            See: https://github.com/mitchtabian/Dagger-Hilt-Playerground/blob/Basic-MVI-Repository-Pattern/
                 app/src/main/java/com/codingwithmitch/daggerhiltplayground/repository/MainRepository.kt
            */

            emit(DataState.Success(networkExplore))
        } catch (e : IOException) {
            emit(DataState.Error(e))
        } catch (e : HttpException) {
            emit(DataState.Error(e))
        }
    }

}