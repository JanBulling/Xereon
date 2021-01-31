package com.xereon.xereon.util

import androidx.annotation.StringRes

sealed class Resource<T>(val data: T?, @StringRes val message: Int?, val errorMessage: String?) {
    class Success<T>(data: T) : Resource<T>(data, null, null)
    class Error<T>(@StringRes messageId: Int? = null, errorMessage: String? = null) : Resource<T>(null, messageId, errorMessage)
}