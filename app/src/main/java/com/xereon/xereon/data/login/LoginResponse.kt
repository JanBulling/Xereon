package com.xereon.xereon.data.login

import com.google.gson.annotations.SerializedName
import com.xereon.xereon.util.Constants

data class LoginResponse(
    @SerializedName("code") val responseCode: Int = Constants.LoginResponseCodes.ERROR.index,
    @SerializedName("uid") val userID: Int = Constants.DEFAULT_USER_ID,
    @SerializedName("verfied") val isVerified: Boolean = false,
)