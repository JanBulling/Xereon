package com.xereon.xereon.util

object Constants {

    const val TAG = "[APP_DEBUG]"

    const val MAX_NUMBER_STORES_ON_MAP = 200

    const val ORDER_DEFAULT = 0
    const val ORDER_NAME_A_Z = 1
    const val ORDER_NAME_Z_A = 2
    const val ORDER_PRICE_LOW = 3
    const val ORDER_PRICE_HIGH = 4
    const val ORDER_ONLY_IN_APP = 5

    const val PREFERENCES_NAME =" xereon_pref"

    const val PREF_APPLICATION_STATE = "APPLICATION_STATE"
    const val PREF_USER_ID = "USER_ID"
    const val PREF_LOCATION_LAT = "START_LOCATION_LATITUDE"
    const val PREF_LOCATION_LNG = "START_LOCATION_LONGITUDE"
    const val PREF_LOCATION_CITY = "START_LOCATION_CITY_NAME"
    const val PREF_LOCATION_ZIP = "START_LOCATION_ZIP_CODE"

    const val DEFAULT_USER_ID = 1
    const val DEFAULT_ZIP = "89547"
    const val DEFAULT_LAT = 48.627866 //50.5943186
    const val DEFAULT_LNG = 10.189545 //9.9428628
    const val DEFAULT_ZOOM = 13f

    const val APPLICATION_STATE_LOGIN = 0       //very first time the app is opened. Not logged in yet
    const val APPLICATION_STATE_TUTORIAL = 1    //login has been skipped or is over -> tutorial has not yet been visited
    const val APPLICATION_STATE_NOT_VERIFIED = 2     //logged in but nor verified


    const val APPLICATION_STATE_VERIFIED = 3

    //verified account
    const val APPLICATION_STATE_SKIP_LOGIN_TUTORIAL = 4

    //login skipped but tutorial not yet ready
    const val APPLICATION_STATE_SKIP_LOGIN = 5 //Skiped login -> app normal but not order
}