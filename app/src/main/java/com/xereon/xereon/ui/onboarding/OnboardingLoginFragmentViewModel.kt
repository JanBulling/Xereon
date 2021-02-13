package com.xereon.xereon.ui.onboarding

import androidx.hilt.lifecycle.ViewModelInject
import com.xereon.xereon.R
import com.xereon.xereon.data.repository.LoginRepository
import com.xereon.xereon.util.InputValidator
import com.xereon.xereon.util.Resource
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.viewmodel.XereonViewModel

class OnboardingLoginFragmentViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
) : XereonViewModel() {

    val loginEvent: SingleLiveEvent<OnboardingEvents> = SingleLiveEvent()

    fun onBackButtonClick() {
        loginEvent.postValue(OnboardingEvents.NavigateBack)
    }

    fun onForgotPasswordClick() {
        loginEvent.postValue(OnboardingEvents.NavigateToForgotPassword)
    }

    fun onChooseRegisterClick() {
        loginEvent.postValue(OnboardingEvents.NavigateToRegister)
    }

    fun onLoginButtonClick(email: String, password: String) {
        launch {
            try {
                if (!InputValidator.validateEmail(email))
                    loginEvent.postValue(OnboardingEvents.Error(R.string.no_email_entered_exception))
                else if (!InputValidator.validatePassword(password))
                    loginEvent.postValue(OnboardingEvents.Error(R.string.no_password_enetred_exception))
                else {
                    loginEvent.postValue(OnboardingEvents.Loading)
                    when (val response = loginRepository.login(email, password, "aa-bb-cc")) {
                        is Resource.Success ->
                            loginEvent.postValue(OnboardingEvents.Success(response.data!!))
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