package com.xereon.xereon.ui.launcher

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xereon.xereon.storage.LocalData
import com.xereon.xereon.update.UpdateChecker
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.viewmodel.XereonViewModel
import kotlinx.coroutines.launch

class LauncherActivityViewModel @ViewModelInject constructor(
    private val updateChecker: UpdateChecker,
    private val localData: LocalData
) : XereonViewModel() {

    val events = SingleLiveEvent<LauncherEvent>()

    init {
        Log.d("APP_DEBUG", "stared LauncherActivityViewModel")

        viewModelScope.launch {
            val updateResult = updateChecker.checkForUpdate()
            Log.d("APP_DEBUG", "updateResults: $updateResult")
            when {
                updateResult.isUpdateNeeded -> LauncherEvent.ShowUpdateDialog(updateResult.updateIntent?.invoke()!!)
                localData.isLocationSet() -> LauncherEvent.GoToMainActivity
                localData.isOnboarded() -> LauncherEvent.GoToChooseLocation
                else -> LauncherEvent.GoToOnboarding
            }.let { events.postValue(it) }
        }
    }

    companion object {
        private val TAG = LauncherActivityViewModel::class.simpleName
    }
}