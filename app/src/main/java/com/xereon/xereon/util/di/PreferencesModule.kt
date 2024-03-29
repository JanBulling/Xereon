package com.xereon.xereon.util.di

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
object PreferencesModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
}