package com.xereon.xereon.storage

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        app: Application,
    ) = Room.databaseBuilder(app, XereonDatabase::class.java, "xereon_database")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideOrderProductDao(db: XereonDatabase) = db.orderProductDao()

    @Provides
    @Singleton
    fun provideFavoriteStoreDao(db: XereonDatabase) = db.favoriteStoreDao()

}