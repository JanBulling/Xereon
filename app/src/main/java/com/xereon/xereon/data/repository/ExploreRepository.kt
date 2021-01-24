package com.xereon.xereon.data.repository

import com.xereon.xereon.data.model.ExploreData
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Resource
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExploreRepository @Inject constructor(private val xereonAPI: XereonAPI) {

    suspend fun getExploreData(userID: Int, zip: String): Resource<ExploreData> {
        return try {
            val response = xereonAPI.getExploreData(userID = userID, postalCode = zip, version = 1)
            val result = response.body()
            if (response.isSuccessful && result != null)
                Resource.Success(result)
            else
                Resource.Error(response.message())

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ein unerwarteter Fehler ist aufgetreten")
        }
    }

}