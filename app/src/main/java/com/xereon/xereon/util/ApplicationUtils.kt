package com.xereon.xereon.util

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
}