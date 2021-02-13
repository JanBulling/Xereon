package com.xereon.xereon.ui.launcher

import android.content.Intent

sealed class LauncherEvent {
    object GoToOnboarding : LauncherEvent()
    object GoToChooseLocation : LauncherEvent()
    object GoToMainActivity : LauncherEvent()
    data class ShowUpdateDialog(
        val updateIntent: Intent
    ) : LauncherEvent()
}