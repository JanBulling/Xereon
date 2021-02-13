package com.xereon.xereon.ui.onboarding

import androidx.annotation.StringRes
import com.xereon.xereon.data.repository.LoginRepository

sealed class OnboardingEvents {
    object NavigateToRegister : OnboardingEvents()
    object NavigateToLogin : OnboardingEvents()
    object NavigateToForgotPassword : OnboardingEvents()

    object NavigateBack : OnboardingEvents()

    object NavigateToLocation : OnboardingEvents()

    object NavigateToMainActivity : OnboardingEvents()

    object NavigateToPrivacy : OnboardingEvents()

    data class Error(@StringRes val message: Int) : OnboardingEvents()
    data class Success(val data: LoginRepository.LoginResponse) : OnboardingEvents()
    object Loading : OnboardingEvents()
}