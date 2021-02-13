package com.xereon.xereon.util

import com.xereon.xereon.ui.onboarding.OnboardingForgotPasswordViewModel
import java.lang.Exception
import java.util.regex.Pattern

object InputValidator {

    /**
     * Checks if the given name is valid ("Mr." + "...")
     * @return true if the name is a valid name
     */
    fun validateName(name: CharSequence): Boolean {
        return try {
            val userName = name.split(" ")[1]
            userName.isNotBlank()
        } catch (e: IndexOutOfBoundsException){ false }
    }

    /**
     * @return true if the given email is valid
     */
    fun validateEmail(email: CharSequence): Boolean {
        if (email.trim().length < 5) return false
        val pattern = Pattern.compile("^.+@.+\\..+$")
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    /**
     * @param minLength optional parameter for the minimum length of the password
     * @return true if the given password has at least the length of minLength
     */
    fun validatePassword(password: CharSequence, minLength: Int = 0): Boolean {
        if (minLength == 0) return password.isNotBlank()
        return password.length >= minLength
    }
}

