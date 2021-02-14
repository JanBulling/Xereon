package com.xereon.xereon.data.store.source

import com.xereon.xereon.data.model.Store
import com.xereon.xereon.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreDataProvider @Inject constructor(
    private val server: StoreDataServer,
) {

    suspend fun getStoreData(storeId: Int): Resource<Store> = server.getStoreData(storeId)

}