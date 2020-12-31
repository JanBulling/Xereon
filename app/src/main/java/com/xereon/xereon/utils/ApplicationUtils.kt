package com.xereon.xereon.utils

import java.util.*
import java.util.regex.Pattern

object ApplicationUtils {
    /**
     * Checks, if the inputted email has a valid format
     * @param email the email as text
     * @return if the email is valid
     */
    fun validateEmail(email: CharSequence): Boolean {
        if (email.length < 5) return false
        val pattern = Pattern.compile("^.+@.+\\..+$")
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    const val DEFAULT_USER_ID = 1

    /**
     * Generates an API-Key on the flight with the userID and the current time -> Always different
     * @param userID id of the user
     * @return Pattern: xxxxxxxx-xxxx-xxxx  (0-9, A, B, C, D, E, F)
     */
    fun generateAPIkey(userID: Int): String {
        val minute = Calendar.getInstance()[Calendar.MINUTE] + 1
        val msb = userID * minute * 2718281828459L
        return digits(msb shr 32, 8) + "-" + digits(msb shr 16, 4) + "-" + digits(
            msb,
            4
        )
    }

    private fun digits(value: Long, digits: Int): String {
        val plc = 1L shl digits * 4
        return java.lang.Long.toHexString(plc or (value and plc - 1)).substring(1)
    }

    const val DEFAULT_ZIP = "89547"
    const val DEFAULT_LAT = 50.5943186f
    const val DEFAULT_LNG = 9.9428628f
    const val PREF_APPLICATION_STATE = "APPLICATION_STATE"
    const val PREF_USER_ID = "USER_ID"
    const val PREF_LOCATION_LAT = "START_LOCATION_LATITUDE"
    const val PREF_LOCATION_LNG = "START_LOCATION_LONGITUDE"
    const val PREF_LOCATION_CITY = "START_LOCATION_CITY_NAME"
    const val PREF_LOCATION_ZIP = "START_LOCATION_ZIP_CODE"
    const val APPLICATION_STATE_LOGIN = 0

    //very first time the app is opened. Not logged in yet
    const val APPLICATION_STATE_TUTORIAL = 1

    //login has been skipped or is over -> tutorial has not yet been visited
    const val APPLICATION_STATE_NOT_VERIFIED = 2

    //loged in but nor verified
    const val APPLICATION_STATE_VERIFIED = 3

    //verified account
    const val APPLICATION_STATE_SKIP_LOGIN_TUTORIAL = 4

    //login skipped but tutorial not yet ready
    const val APPLICATION_STATE_SKIP_LOGIN = 5 //Skiped login -> app normal but not order
}