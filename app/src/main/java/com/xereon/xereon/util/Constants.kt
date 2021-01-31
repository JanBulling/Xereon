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

    enum class SortType(val index: Int) {
        RESPONSE_NEW_FIRST(1),      //Latest added products / stores first
        RESPONSE_OLD_FIRST(2),      //the first products / stores added
        RESPONSE_A_Z(3),            //name from a to z
        RESPONSE_Z_A(4),            //name from z to a
        RESPONSE_PRICE_LOW(5),      //products with lowest price first
        RESPONSE_PRICE_HIGH(6),     //products with highest price first
        RESPONSE_ONLY_IN_APP(7)     //only app-exclusive products
    }

    enum class ApplicationState(val index: Int) {
        FIRST_OPENED(0),
        SKIPPED_AND_NO_LOCATION(1),
        LOGGED_IN_AND_NO_LOCATION(2),
        SKIPPED_AND_LOCATION(3),
        LOGGED_IN_AND_LOCATION(4),
        VALID_USER_ACCOUNT(5);

        companion object {
            fun fromInt(index: Int) = values().first { it.index == index }
        }
    }

    enum class LoginResponseCodes(val index: Int) {
        SUCCESS(0),
        WRONG_LOGIN_DATA(1),
        EMAIL_ALREADY_REGISTERED(2),
        ERROR(3)
        ;

        companion object {
            fun fromInt(index: Int) =
                if (index <= 3) values().first { it.index == index }
                else ERROR
        }
    }
}