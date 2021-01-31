package com.xereon.xereon.ui.login

import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xereon.xereon.R
import com.xereon.xereon.data.repository.LoginRepository
import com.xereon.xereon.data.repository.LoginResponse
import com.xereon.xereon.util.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class LoginViewModel @ViewModelInject constructor(
    private val repository: LoginRepository,
) : ViewModel() {

    sealed class LoginEvent {
        data class Success(val loginData: LoginResponse) : LoginEvent()
        data class LoginError(@StringRes val messageId: Int) : LoginEvent()
        object Loading : LoginEvent()
    }

    private val _loginData: MutableLiveData<LoginEvent> = MutableLiveData()
    val loginData: LiveData<LoginEvent> get() = _loginData

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
                    is Resource.Error ->
                        _loginData.value = LoginEvent.LoginError(response.message!!)
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
                _eventChannel.send(LoginEvent.LoginError(R.string.unexprected_exception))
            }
        } catch (e: Exception) {
            e.printStackTrace()
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