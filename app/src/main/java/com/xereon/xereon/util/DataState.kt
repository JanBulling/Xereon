package com.xereon.xereon.util

sealed class DataState<out R> {
    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val message: String) : DataState<Nothing>()
    object Loading : DataState<Nothing>()

    companion object {
        const val SUCCESS_INDEX = 0
        const val LOADING_INDEX = 1
        const val ERROR_INDEX = 2
    }
}