package com.xereon.xereon.data.repository

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.xereon.xereon.R
import com.xereon.xereon.network.XereonAPI
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Constants.LoginResponseCodes
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.Resource
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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
                Log.d(TAG, "Code: ${result.responseCode}")
                when (LoginResponseCodes.fromInt(result.responseCode)) {
                    LoginResponseCodes.SUCCESS -> Resource.Success(result)
                    LoginResponseCodes.WRONG_LOGIN_DATA -> Resource.Error(R.string.login_data_wrong_exception)
                    LoginResponseCodes.EMAIL_ALREADY_REGISTERED -> Resource.Error(R.string.email_already_registerd_exception)
                    LoginResponseCodes.ERROR -> Resource.Error(R.string.unexpected_exception)
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
            val response = xereonAPI.register(
                name = name,
                email = email,
                password = password,
                token = firebaseToke
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Log.d(TAG, "Code: ${result.responseCode}")
                when (LoginResponseCodes.fromInt(result.responseCode)) {
                    LoginResponseCodes.SUCCESS -> Resource.Success(result)
                    LoginResponseCodes.WRONG_LOGIN_DATA -> Resource.Error(R.string.login_data_wrong_exception)
                    LoginResponseCodes.EMAIL_ALREADY_REGISTERED -> Resource.Error(R.string.email_already_registerd_exception)
                    LoginResponseCodes.ERROR -> Resource.Error(R.string.unexpected_exception)
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
            val response = xereonAPI.resetPassword(email)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Log.d(TAG, "Code: ${result.responseCode}")
                when (LoginResponseCodes.fromInt(result.responseCode)) {
                    LoginResponseCodes.SUCCESS -> Resource.Success(result)
                    LoginResponseCodes.WRONG_LOGIN_DATA -> Resource.Error(R.string.login_data_wrong_exception)
                    LoginResponseCodes.EMAIL_ALREADY_REGISTERED -> Resource.Error(R.string.email_already_registerd_exception)
                    LoginResponseCodes.ERROR -> Resource.Error(R.string.unexpected_exception)
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

    data class LoginResponse(
        @SerializedName("code") val responseCode: Int = LoginResponseCodes.ERROR.index,
        @SerializedName("uid") val userID: Int = Constants.DEFAULT_USER_ID,
        @SerializedName("verfied") val isVerified: Boolean = false,
    )
}