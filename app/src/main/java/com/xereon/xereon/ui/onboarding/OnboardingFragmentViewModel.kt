package com.xereon.xereon.ui.onboarding

import androidx.hilt.lifecycle.ViewModelInject
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.viewmodel.XereonViewModel

class OnboardingFragmentViewModel @ViewModelInject constructor() : XereonViewModel() {

    val routeToScreen: SingleLiveEvent<OnboardingEvents> = SingleLiveEvent()

    fun onChooseLoginClicked() {
        routeToScreen.postValue(OnboardingEvents.NavigateToLogin)
    }

    fun onChooseCreateAccountClicked() {
        routeToScreen.postValue(OnboardingEvents.NavigateToRegister)
    }

    fun onSkippedButtonClick() {
        routeToScreen.postValue(OnboardingEvents.NavigateToLocation)
    }

}