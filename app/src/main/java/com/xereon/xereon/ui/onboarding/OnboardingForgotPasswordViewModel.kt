package com.xereon.xereon.ui.onboarding

import androidx.hilt.lifecycle.ViewModelInject
import com.xereon.xereon.R
import com.xereon.xereon.data.login.LoginResponse
import com.xereon.xereon.data.login.source.LoginProvider
import com.xereon.xereon.util.InputValidator
import com.xereon.xereon.util.Resource
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.viewmodel.XereonViewModel

class OnboardingForgotPasswordViewModel @ViewModelInject constructor(
    private val loginProvider: LoginProvider,
) : XereonViewModel() {

    val loginEvent: SingleLiveEvent<OnboardingEvents> = SingleLiveEvent()

    fun onBackButtonClick() {
        loginEvent.postValue(OnboardingEvents.NavigateBack)
    }

    fun onForgotPasswordClick(email: String) {
        launch {
            try {
                if (!InputValidator.validateEmail(email))
                    loginEvent.postValue(OnboardingEvents.Error(R.string.no_email_entered_exception))
                else {
                    loginEvent.postValue(OnboardingEvents.Loading)
                    when (val response = loginProvider.resetPassword(email)) {
                        is Resource.Success ->
                            loginEvent.postValue(OnboardingEvents.Success(LoginResponse()))
                        is Resource.Error -> {
                            loginEvent.postValue(OnboardingEvents.Error(response.message!!))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                loginEvent.postValue(OnboardingEvents.Error(R.string.unexpected_exception))
            }
        }
    }
}