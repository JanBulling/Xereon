package com.xereon.xereon.data.login.source

import android.util.Log
import com.bumptech.glide.load.HttpException
import com.xereon.xereon.R
import com.xereon.xereon.data.login.LoginResponse
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Resource
import dagger.Reusable
import java.io.IOException
import javax.inject.Inject

@Reusable
class LoginServer @Inject constructor(
    private val api: XereonAPI
) {

    suspend fun login(email: String, password: String, firebaseToke: String): Resource<LoginResponse> {
        return try {
            val response = api.login(
                email = email,
                password = password,
                token = firebaseToke
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Log.d(TAG, "Code: ${result.responseCode}")
                when (Constants.LoginResponseCodes.fromInt(result.responseCode)) {
                    Constants.LoginResponseCodes.SUCCESS -> Resource.Success(result)
                    Constants.LoginResponseCodes.WRONG_LOGIN_DATA -> Resource.Error(R.string.login_data_wrong_exception)
                    Constants.LoginResponseCodes.EMAIL_ALREADY_REGISTERED -> Resource.Error(R.string.email_already_registerd_exception)
                    Constants.LoginResponseCodes.ERROR -> Resource.Error(R.string.unexpected_exception)
                }
            } else
                Resource.Error(R.string.unexpected_exception)

        } catch (e: Exception) {
            Log.e(TAG, "Error in Repository: ${e.stackTraceToString()}")
            when (e) {
                is HttpException, is IOException -> Resource.Error(R.string.no_connection_exception)
                else -> Resource.Error(R.string.unexpected_exception)
            }
        }
    }

    suspend fun createUser(name: String, email: String, password: String, firebaseToke: String): Resource<LoginResponse> {
        return try {
            val response = api.register(
                name = name,
                email = email,
                password = password,
                token = firebaseToke
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Log.d(TAG, "Code: ${result.responseCode}")
                when (Constants.LoginResponseCodes.fromInt(result.responseCode)) {
                    Constants.LoginResponseCodes.SUCCESS -> Resource.Success(result)
                    Constants.LoginResponseCodes.WRONG_LOGIN_DATA -> Resource.Error(R.string.login_data_wrong_exception)
                    Constants.LoginResponseCodes.EMAIL_ALREADY_REGISTERED -> Resource.Error(R.string.email_already_registerd_exception)
                    Constants.LoginResponseCodes.ERROR -> Resource.Error(R.string.unexpected_exception)
                }
            } else
                Resource.Error(R.string.unexpected_exception)

        } catch (e: Exception) {
            Log.e(TAG, "Error in Repository: ${e.stackTraceToString()}")
            when (e) {
                is HttpException, is IOException -> Resource.Error(R.string.no_connection_exception)
                else -> Resource.Error(R.string.unexpected_exception)
            }
        }
    }

    suspend fun resetPassword(email: String): Resource<LoginResponse> {
        return try {
            val response = api.resetPassword(email)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Log.d(TAG, "Code: ${result.responseCode}")
                when (Constants.LoginResponseCodes.fromInt(result.responseCode)) {
                    Constants.LoginResponseCodes.SUCCESS -> Resource.Success(result)
                    Constants.LoginResponseCodes.WRONG_LOGIN_DATA -> Resource.Error(R.string.login_data_wrong_exception)
                    Constants.LoginResponseCodes.EMAIL_ALREADY_REGISTERED -> Resource.Error(R.string.email_already_registerd_exception)
                    Constants.LoginResponseCodes.ERROR -> Resource.Error(R.string.unexpected_exception)
                }
            } else
                Resource.Error(R.string.unexpected_exception)

        } catch (e: Exception) {
            Log.e(TAG, "Error in Repository: ${e.stackTraceToString()}")
            when (e) {
                is HttpException, is IOException -> Resource.Error(R.string.no_connection_exception)
                else -> Resource.Error(R.string.unexpected_exception)
            }
        }
    }

    companion object {
        private const val TAG = "LoginServer"
    }
}