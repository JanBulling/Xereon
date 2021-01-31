package com.xereon.xereon.data.repository

import android.util.Log
import com.xereon.xereon.R
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Constants.LoginResponseCodes
import com.xereon.xereon.util.Resource
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class LoginRepository @Inject constructor(private val xereonAPI: XereonAPI) {

    suspend fun login(email: String, password: String, firebaseToke: String): Resource<LoginResponse> {
        return try {
            val response = xereonAPI.login(
                email = email,
                password = password,
                token = firebaseToke
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                when (LoginResponseCodes.fromInt(result.responseCode)) {
                    LoginResponseCodes.SUCCESS -> Resource.Success(result)
                    LoginResponseCodes.WRONG_LOGIN_DATA -> Resource.Error(R.string.login_data_wrong_exception)
                    LoginResponseCodes.EMAIL_ALREADY_REGISTERED -> Resource.Error(R.string.email_already_registerd_exception)
                    LoginResponseCodes.ERROR -> Resource.Error(R.string.unexprected_exception)
                }
            } else
                Resource.Error(R.string.unexprected_exception)

        } catch (e: Exception) {
            Log.e(Constants.TAG, "Error in Repository: ${e.stackTraceToString()}")
            when (e) {
                is HttpException -> Resource.Error(R.string.no_connection_exception)
                is IOException -> Resource.Error(R.string.no_connection_exception)
                else -> Resource.Error(R.string.unexprected_exception)
            }
        }
    }

}

data class LoginResponse(
    val responseCode: Int = LoginResponseCodes.ERROR.index,
    val responseMessage: String,
    val uid: Int = Constants.DEFAULT_USER_ID,
    val isVerified: Boolean = false,
)