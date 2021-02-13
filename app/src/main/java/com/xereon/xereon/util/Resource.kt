package com.xereon.xereon.util

import androidx.annotation.StringRes

sealed class Resource<T>(val data: T?, @StringRes val message: Int?) {
    class Success<T>(data: T) : Resource<T>(data, null)
    class Error<T>(@StringRes messageId: Int) : Resource<T>(null, messageId)
}