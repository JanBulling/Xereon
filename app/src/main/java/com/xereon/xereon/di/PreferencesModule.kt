package com.xereon.xereon.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLng
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Constants.PREF_APPLICATION_STATE
import com.xereon.xereon.util.Constants.PREF_LOCATION_CITY
import com.xereon.xereon.util.Constants.SHARED_PREFERENCES_NAME
import com.xereon.xereon.util.Constants.PREF_LOCATION_LAT
import com.xereon.xereon.util.Constants.PREF_LOCATION_LNG
import com.xereon.xereon.util.Constants.PREF_LOCATION_POSTCODE
import com.xereon.xereon.util.Constants.ApplicationState
import com.xereon.xereon.util.Constants.PREF_USER_ID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Qualifier
import javax.inject.Singleton

//NOTE: SharedPref might be deprecated in the future

@Module
@InstallIn(ApplicationComponent::class)
object PreferencesAppModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    @ProvideUserId
    fun provideUserId(sharedPref: SharedPreferences): Int =
        sharedPref.getInt(PREF_USER_ID, Constants.DEFAULT_USER_ID)

    @Singleton
    @Provides
    @ProvidePostCode
    fun providePostCode(sharedPref: SharedPreferences): String =
        sharedPref.getString(PREF_LOCATION_POSTCODE, Constants.DEFAULT_POSTCODE)
            ?: Constants.DEFAULT_POSTCODE

    @Singleton
    @Provides
    @ProvideCity
    fun provideCity(sharedPref: SharedPreferences) =
        sharedPref.getString(PREF_LOCATION_CITY, Constants.DEFAULT_CITY) ?: Constants.DEFAULT_CITY

    @Singleton
    @Provides
    @ProvideLatLng
    fun provideLatLng(sharedPref: SharedPreferences): LatLng {
        val lat = sharedPref.getFloat(PREF_LOCATION_LAT, Constants.DEFAULT_LAT)
        val lng = sharedPref.getFloat(PREF_LOCATION_LNG, Constants.DEFAULT_LNG)
        return LatLng(lat.toDouble(), lng.toDouble())
    }

    @Singleton
    @Provides
    @ProvideApplicationState
    fun provideApplicationState(sharedPref: SharedPreferences): ApplicationState =
        ApplicationState.fromInt(sharedPref.getInt(PREF_APPLICATION_STATE, 0))
}

@Qualifier @Retention(AnnotationRetention.BINARY) annotation class ProvideUserId
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class ProvidePostCode
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class ProvideCity
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class ProvideLatLng
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class ProvideApplicationState