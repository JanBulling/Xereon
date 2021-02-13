package com.xereon.xereon.di

import com.xereon.xereon.network.AlgoliaPlacesApi
import com.xereon.xereon.network.IPLocationAPI
import com.xereon.xereon.network.XereonAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    @InjectXereonAPI
    fun provideRetrofit() : Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val configClient = OkHttpClient.Builder().apply {
            addInterceptor(logging)
        }.build()

        return Retrofit.Builder()
            .baseUrl(XereonAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(configClient)
            .build()
    }
    @Singleton
    @Provides
    fun provideXereonAPI(@InjectXereonAPI retrofit: Retrofit): XereonAPI =
        retrofit.create(XereonAPI::class.java)


    @Singleton
    @Provides
    @InjectAlgoliaAPI
    fun providePlacesRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(AlgoliaPlacesApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Singleton
    @Provides
    fun provideAlgoliaAPI(@InjectAlgoliaAPI retrofit: Retrofit): AlgoliaPlacesApi =
        retrofit.create(AlgoliaPlacesApi::class.java)


    @Singleton
    @Provides
    @IPLocationAnnotation
    fun provideIPLocationRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(IPLocationAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Singleton
    @Provides
    fun provideIPLocationAPI(@IPLocationAnnotation retrofit: Retrofit): IPLocationAPI =
        retrofit.create(IPLocationAPI::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InjectAlgoliaAPI

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IPLocationAnnotation

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InjectXereonAPI

