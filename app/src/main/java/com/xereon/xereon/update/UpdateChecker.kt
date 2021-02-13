package com.xereon.xereon.update

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.xereon.xereon.BuildConfig
import com.xereon.xereon.network.XereonAPI
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateChecker @Inject constructor(
    private val api: XereonAPI,
) {

    suspend fun checkForUpdate(): Result= try {
        if (isUpdateNeeded()) {
            Result(isUpdateNeeded = true, updateIntent = createUpdateAction())
        } else {
            Result(isUpdateNeeded = false)
        }
    } catch (exception: Exception) {
        Log.e(TAG, "Exception caught: " + exception.localizedMessage)
        Result(isUpdateNeeded = false)
    }

    private suspend fun isUpdateNeeded(): Boolean {

        val response = api.getMinVersion()
        if (!response.isSuccessful) throw HttpException(response)

        val minVersionFromServer = requireNotNull(response.body()) { "Response was successful but body was null" }

        val currentVersion = BuildConfig.VERSION_CODE

        Log.d(TAG, "minVersionFromServer: $minVersionFromServer")
        Log.d(TAG, "Current app version: $currentVersion")

        val needsImmediateUpdate = VersionComparator.isVersionOlder(
            currentVersion,
            minVersionFromServer.minSdkVersion
        )
        Log.e(TAG, "needs update:$needsImmediateUpdate")
        return needsImmediateUpdate
    }

    private fun createUpdateAction(): () -> Intent = {
        val uriStringInPlayStore = STORE_PREFIX + BuildConfig.APPLICATION_ID
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(uriStringInPlayStore)
            setPackage(COM_ANDROID_VENDING)
        }
    }

    data class Response(
        val currentVersion: Int,
        val minSdkVersion: Int,
    )

    data class Result(
        val isUpdateNeeded: Boolean,
        val updateIntent: (() -> Intent)? = null
    )

    companion object {
        private const val TAG: String = "UpdateChecker"

        private const val STORE_PREFIX = "https://play.google.com/store/apps/details?id="
        private const val COM_ANDROID_VENDING = "com.android.vending"
    }
}