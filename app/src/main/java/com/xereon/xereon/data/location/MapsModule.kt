package com.xereon.xereon.data.location

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object MapsModule {

    @Singleton
    @Provides
    @MapsCache
    fun provideCacheDir(
        @ApplicationContext context: Context
    ): File = File(context.cacheDir, "maps")

}

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class MapsCache