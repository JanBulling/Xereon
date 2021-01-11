package com.xereon.xereon.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.data.paging.StoresPagingSource
import com.xereon.xereon.data.paging.StoresPagingSource.Companion.SEARCH_BY_TYPE
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
class CategoryRepository @Inject constructor(private val xereonAPI: XereonAPI) {

    suspend fun getExampleStoresForCategory(category : Int, zip: String): Flow<DataState<List<SimpleStore>>> = flow {
        emit(DataState.Loading)
        try {
            delay(500)
            val networkExplore = xereonAPI.searchStoreByCategory(category, zip, 10, 1)

            emit(DataState.Success(networkExplore))
        } catch (e : Exception) {
            when (e) {
                is IOException, is HttpException -> emit(DataState.Error("Keine Verbindung"))
                else -> emit(DataState.Error("Es ist ein Fehler aufgetreten"))
            }
        }
    }

    fun getStoresByType(type: String, query: String, zip: String) =
        Pager(
            config = PagingConfig(
                initialLoadSize = 10,
                pageSize = 10,
                maxSize = 100,
                prefetchDistance = 1,
                enablePlaceholders = false
            ), pagingSourceFactory = { StoresPagingSource(xereonAPI, query = query, zip = zip, searchType = SEARCH_BY_TYPE, type = type) }
        ).liveData

}