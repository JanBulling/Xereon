package com.xereon.xereon.di

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import com.xereon.xereon.db.XereonDatabase
import com.xereon.xereon.network.AlgoliaPlacesApi
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

    @Singleton
    @Provides
    @XereonAnnotation
    fun provideRetrofit() : Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        return Retrofit.Builder()
                .baseUrl(XereonAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
    }
    @Singleton
    @Provides
    fun provideXereonAPI(@XereonAnnotation retrofit: Retrofit): XereonAPI =
        retrofit.create(XereonAPI::class.java)


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Singleton
    @Provides
    @AlgoliaAnnotation
    fun providePlacesRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(AlgoliaPlacesApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Singleton
    @Provides
    fun provideAlgoliaAPI(@AlgoliaAnnotation retrofit: Retrofit): AlgoliaPlacesApi =
        retrofit.create(AlgoliaPlacesApi::class.java)


    ////////////////////////////////////////////////////////////////////////////////////////////////
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


    ////////////////////////////////////////////////////////////////////////////////////////////////


    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////  QUALIFIER ANNOTATIONS  ////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AlgoliaAnnotation

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class XereonAnnotation

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope