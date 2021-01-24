package com.xereon.xereon.util

object Constants {
    const val TAG = "[APP_DEBUG]"
    const val MAX_NUMBER_STORES_ON_MAP = 200

    const val PREFERENCES_NAME =" xereon_pref"

    const val PREF_USER_ID = "USER_ID"
    const val PREF_LOCATION_LAT = "START_LOCATION_LATITUDE"
    const val PREF_LOCATION_LNG = "START_LOCATION_LONGITUDE"
    const val PREF_LOCATION_CITY = "START_LOCATION_CITY_NAME"
    const val PREF_LOCATION_ZIP = "START_LOCATION_ZIP_CODE"

    const val DEFAULT_USER_ID = 1
    const val DEFAULT_ZIP = "89522"  //Herbrechtingen: "89542"
    const val DEFAULT_LAT = 48.6761321216   //Herbrechtingen: 48.627866     //Nürnberg: 50.5943186
    const val DEFAULT_LNG = 10.1578746044   //Herbrechtingen: 10.189545     //Nürnberg: 9.9428628
    const val DEFAULT_ZOOM = 13f

    enum class SortTypes(val index: Int) {
        SORT_RESPONSE_DEFAULT(0),
        SORT_RESPONSE_A_Z(1),
        SORT_RESPONSE_Z_A(2),
        SORT_RESPONSE_PRICE_LOW(3),
        SORT_RESPONSE_PRICE_HIGH(4),
        SORT_RESPONSE_ONLY_IN_APP(5),
    }
}