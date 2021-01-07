package com.xereon.xereon.data.repository

import com.xereon.xereon.data.model.ExploreData
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.DataState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExploreRepository @Inject constructor(private val xereonAPI: XereonAPI) {

    suspend fun getExploreData(userID: Int, zip : String): Flow<DataState<ExploreData>> = flow {
        emit(DataState.Loading)
        try {
            delay(500)
            val networkExplore = xereonAPI.getExploreData(userID, zip, 1)

            /*
            Here implement caching.
            See: https://github.com/mitchtabian/Dagger-Hilt-Playerground/blob/Basic-MVI-Repository-Pattern/
                 app/src/main/java/com/codingwithmitch/daggerhiltplayground/repository/MainRepository.kt
            */

            emit(DataState.Success(networkExplore))
        } catch (e : Exception) {
            when (e) {
                is IOException, is HttpException -> emit(DataState.Error("Keine Verbindung"))
                else -> emit(DataState.Error("Es ist ein Fehler aufgetreten"))
            }
        }
    }

}