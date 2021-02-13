package com.xereon.xereon.ui.onboarding

import androidx.hilt.lifecycle.ViewModelInject
import com.xereon.xereon.R
import com.xereon.xereon.data.repository.LoginRepository
import com.xereon.xereon.util.InputValidator
import com.xereon.xereon.util.Resource
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.viewmodel.XereonViewModel
import java.lang.Exception

class OnboardingRegisterFragmentViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
) : XereonViewModel() {

    val loginEvent: SingleLiveEvent<OnboardingEvents> = SingleLiveEvent()

    fun onBackClick() {
        loginEvent.postValue(OnboardingEvents.NavigateBack)
    }

    fun onChooseLoginClick() {
        loginEvent.postValue(OnboardingEvents.NavigateToLogin)
    }

    fun onRegisterClick(name: String, email: String, password: String) {
        launch {
            try {
                if (!InputValidator.validateName(name))
                    loginEvent.postValue(OnboardingEvents.Error(R.string.no_name_entered_exception))
                if (!InputValidator.validateEmail(email))
                    loginEvent.postValue(OnboardingEvents.Error(R.string.no_email_entered_exception))
                else if (!InputValidator.validatePassword(password, 6))
                    loginEvent.postValue(OnboardingEvents.Error(R.string.no_password_too_short_exception))
                else {
                    loginEvent.postValue(OnboardingEvents.Loading)
                    when (val response = loginRepository.createUser(name, email, password, "aa-bb-cc")) {
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