package com.xereon.xereon.util

object Constants {
    const val TAG = "[APP_DEBUG]"
    const val MAX_NUMBER_STORES_ON_MAP = 200

    const val SHARED_PREFERENCES_NAME =" xereon_pref"

    const val PREF_USER_ID = "xereon.preferences.user.uid"
    const val PREF_LOCATION_LAT = "xereon.preferences.user.lat"
    const val PREF_LOCATION_LNG = "xereon.preferences.user.lng"
    const val PREF_LOCATION_CITY = "xereon.preferences.user.city"
    const val PREF_LOCATION_POSTCODE = "xereon.preferences.user.postcode"
    const val PREF_APPLICATION_STATE = "xereon.preferences.application.state"

    const val DEFAULT_USER_ID = 1
    const val DEFAULT_POSTCODE = "89522"  //Herbrechtingen: "89542"
    const val DEFAULT_CITY = "Heidenheim"
    const val DEFAULT_LAT = 48.6761321216f   //Herbrechtingen: 48.627866     //Nürnberg: 50.5943186
    const val DEFAULT_LNG = 10.1578746044f   //Herbrechtingen: 10.189545     //Nürnberg: 9.9428628
    const val DEFAULT_ZOOM = 13f

    enum class SortTypes(val index: Int) {
        SORT_RESPONSE_NEW_FIRST(1),//Latest added products / stores first
        SORT_RESPONSE_OLD_FIRST(2), //the first products / stores added
        SORT_RESPONSE_A_Z(3),   //name from a to z
        SORT_RESPONSE_Z_A(4),   //name from z to a
        SORT_RESPONSE_PRICE_LOW(5), //products with lowest price first
        SORT_RESPONSE_PRICE_HIGH(6),    //products with highest price first
        SORT_RESPONSE_ONLY_IN_APP(7)    //only app-exclusive products
    }

    enum class ApplicationState(val index: Int) {
        STATE_FIRST_USE(0),
        STATE_TUTORIAL_NOT_COMPLETED(1),
        STATE_LOGIN_SKIPPED(2),
        STATE_HAS_VALID_ACCOUNT(3);

        companion object {
            fun fromInt(index: Int) = values().first { it.index == index }
        }
    }
}