package com.xereon.xereon.ui.login

import android.util.Log
import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xereon.xereon.R
import com.xereon.xereon.data.repository.LoginRepository
import com.xereon.xereon.data.repository.LoginResponse
import com.xereon.xereon.data.repository.PlacesRepository
import com.xereon.xereon.network.response.IPLocationResponse
import com.xereon.xereon.network.response.PlacesRequest
import com.xereon.xereon.network.response.PlacesResponse
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class LoginViewModel @ViewModelInject constructor(
    private val repository: LoginRepository,
    private val placesRepository: PlacesRepository,
) : ViewModel() {

    sealed class LoginEvent {
        data class Success(val loginData: LoginResponse) : LoginEvent()
        data class SuccessIPLocation(val ipLocation: IPLocationResponse) : LoginEvent()
        data class LoginError(@StringRes val messageId: Int) : LoginEvent()
        object Loading : LoginEvent()
    }

    private val _loginData: MutableLiveData<LoginEvent> = MutableLiveData()
    val loginData: LiveData<LoginEvent> get() = _loginData

    private val _places: MutableLiveData<PlacesResponse> = MutableLiveData()
    val places: LiveData<PlacesResponse> get() = _places

    private val _ipPlace: MutableLiveData<LoginEvent> = MutableLiveData()
    val ipPlace: LiveData<LoginEvent> get() = _ipPlace

    var selectedPostCode: String = ""
    var selectedCityName: String = ""
    var selectedLocationLat: Float = 0f
    var selectedLocationLng: Float = 0f

    private val _eventChannel = Channel<LoginEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun performLogin(email: String, password: String) = viewModelScope.launch {
        try {
            if (!validateEmail(email))
                _eventChannel.send(LoginEvent.LoginError(R.string.no_email_entered_exception))
            else if (password.isBlank())
                _eventChannel.send(LoginEvent.LoginError(R.string.no_password_enetred_exception))
            else {
                _loginData.value = LoginEvent.Loading
                when (val response = repository.login(email, password, "")) {
                    is Resource.Success ->
                        _loginData.value = LoginEvent.Success(response.data!!)
                    is Resource.Error -> {
                        _loginData.value = LoginEvent.LoginError(response.message!!)
                        _eventChannel.send(LoginEvent.LoginError(response.message))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _eventChannel.send(LoginEvent.LoginError(R.string.unexprected_exception))
        }
    }

    fun performSignUp(name: String, email: String, password: String) = viewModelScope.launch {
        try {
            if (name.isBlank())
                _eventChannel.send(LoginEvent.LoginError(R.string.no_name_entered_exception))
            else if (!validateEmail(email))
                _eventChannel.send(LoginEvent.LoginError(R.string.no_email_entered_exception))
            else if (password.trim().length < 6)
                _eventChannel.send(LoginEvent.LoginError(R.string.no_password_too_short_exception))
            else {
                _loginData.value = LoginEvent.Loading
                when (val response = repository.createUser(name, email, password, "")) {
                    is Resource.Success ->
                        _loginData.value = LoginEvent.Success(response.data!!)
                    is Resource.Error -> {
                        _loginData.value = LoginEvent.LoginError(response.message!!)
                        _eventChannel.send(LoginEvent.LoginError(response.message))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _eventChannel.send(LoginEvent.LoginError(R.string.unexprected_exception))
        }
    }

    fun autocompletePlacesSearch(query: String)  = viewModelScope.launch {
        try {
            val request = PlacesRequest(
                query = query,
                hitsPerPage = 4
            )
            when (val response = placesRepository.getPlaces(request)) {
                is Resource.Success -> _places.value = response.data
                is Resource.Error -> {
                    _eventChannel.send(LoginEvent.LoginError(response.message!!))
                    _places.value = PlacesResponse(emptyList(), 0, 0, "")
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e(Constants.TAG, "Unexpected error in MapViewModel: ${e.message}")
            _eventChannel.send(LoginEvent.LoginError(R.string.unexprected_exception))
        }
    }

    fun getApproximateDataWithIPAddress() = viewModelScope.launch {
        try {
            when (val response = placesRepository.getApproximatePosition()) {
                is Resource.Success -> {
                    _ipPlace.value = LoginEvent.SuccessIPLocation(response.data!!)
                    selectedPostCode = response.data.postCode
                    selectedCityName = response.data.city
                    selectedLocationLat = response.data.latitude
                    selectedLocationLng = response.data.longitude
                }
                is Resource.Error -> {
                    _eventChannel.send(LoginEvent.LoginError(response.message!!))
                    _ipPlace.value = LoginEvent.LoginError(response.message)
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e(Constants.TAG, "Unexpected error in MapViewModel: ${e.message}")
            _eventChannel.send(LoginEvent.LoginError(R.string.unexprected_exception))
        }
    }

    private fun validateEmail(email: CharSequence): Boolean {
        if (email.trim().length < 5) return false
        val pattern = Pattern.compile("^.+@.+\\..+$")
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
}