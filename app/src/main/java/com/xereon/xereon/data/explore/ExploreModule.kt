package com.xereon.xereon.data.explore

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Cache
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ExploreModule {

    @Singleton
    @Provides
    @ExploreCache
    fun provideCacheDir(
        @ApplicationContext context: Context
    ): File = File(context.cacheDir, "explore")

}

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class ExploreCache