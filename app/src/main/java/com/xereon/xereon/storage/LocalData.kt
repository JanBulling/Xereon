package com.xereon.xereon.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.android.gms.maps.model.LatLng
import com.xereon.xereon.data.maps.Place
import com.xereon.xereon.data.maps.IPLocation
import com.xereon.xereon.data.login.LoginResponse
import com.xereon.xereon.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalData @Inject constructor(
    private val preferences: SharedPreferences
) {

    companion object {
        private const val PREF_ONBOARDING = "pref.applicationState.onboarding"
        private const val PREF_LOCATION = "pref.applicationState.location"

        private const val PREF_VALID_ACCOUNT = "pref.user.valid"
        private const val PREF_USER_ID = "pref.user.uuid"
        private const val PREF_USER_CITY = "pref.user.city"
        private const val PREF_USER_POSTCODE = "pref.user.postcode"
        private const val PREF_USER_LAT = "pref.user.lat"
        private const val PREF_USER_LNG = "pref.user.lng"
    }

    fun isOnboarded(): Boolean = preferences.getBoolean(PREF_ONBOARDING, false)
    fun setOnborded(value: Boolean) = preferences.edit(true) { putBoolean(PREF_ONBOARDING, value) }

    fun isLocationSet(): Boolean = preferences.getBoolean(PREF_LOCATION, false)
    fun setLocationSet(value: Boolean) = preferences.edit(true) { putBoolean(PREF_LOCATION, value) }

    fun setLoginData(loginData: LoginResponse) {
        preferences.edit(true) {
            putBoolean(PREF_VALID_ACCOUNT, loginData.isVerified)
            putInt(PREF_VALID_ACCOUNT, loginData.userID)
        }
    }

    fun isAccountValid(): Boolean = preferences.getBoolean(PREF_VALID_ACCOUNT, false)
    fun setAccountValid(value: Boolean) = preferences.edit(true) { putBoolean(PREF_VALID_ACCOUNT, value) }

    fun setUserID(value: Int) = preferences.edit(true) { putInt(PREF_USER_ID, value) }
    fun getUserID() : Int = preferences.getInt(PREF_USER_ID, Constants.DEFAULT_USER_ID)

    fun setLocationData(data: IPLocation) {
        preferences.edit(true) {
            putFloat(PREF_USER_LAT, data.latitude)
            putFloat(PREF_USER_LNG, data.longitude)
            putString(PREF_USER_POSTCODE, data.postCode)
            putString(PREF_USER_CITY, data.city)
        }
    }

    fun setLocationData(data: Place) {
        preferences.edit(true) {
            putFloat(PREF_USER_LAT, data.coordinates.latitude)
            putFloat(PREF_USER_LNG, data.coordinates.longitude)
            putString(PREF_USER_POSTCODE, data.postCode)
            putString(PREF_USER_CITY, data.name)
        }
    }

    fun getCity() = preferences.getString(PREF_USER_CITY, Constants.DEFAULT_CITY) ?: Constants.DEFAULT_CITY
    fun getPostCode() = preferences.getString(PREF_USER_POSTCODE, Constants.DEFAULT_POSTCODE) ?: Constants.DEFAULT_POSTCODE
    fun getLatLng() = LatLng(
        preferences.getFloat(PREF_USER_LAT, Constants.DEFAULT_LAT).toDouble(),
        preferences.getFloat(PREF_USER_LNG, Constants.DEFAULT_LNG).toDouble()
    )
}