package com.xereon.xereon.di

import android.content.Context
import android.content.SharedPreferences
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants.PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
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
    fun provideXereonAPI(retrofit: Retrofit) :XereonAPI =
        retrofit.create(XereonAPI::class.java)


    @Singleton
    @Provides
    fun providePreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
}