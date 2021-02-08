package com.xereon.xereon.di

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import com.xereon.xereon.db.XereonDatabase
import com.xereon.xereon.network.AlgoliaPlacesApi
import com.xereon.xereon.network.IPLocationAPI
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope