package com.xereon.xereon.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.xereon.xereon.db.XereonDatabase
import com.xereon.xereon.network.AlgoliaPlacesApi
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants.PREFERENCES_NAME
import com.xereon.xereon.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Singleton
    @Provides
    fun providePreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Singleton
    @Provides
    fun provideDispatchers(): DispatcherProvider = object: DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined

    }
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