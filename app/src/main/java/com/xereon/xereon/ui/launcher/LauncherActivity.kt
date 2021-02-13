package com.xereon.xereon.ui.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.xereon.xereon.ui.main.MainActivity
import com.xereon.xereon.ui.onboarding.OnboardingActivity
import com.xereon.xereon.ui.selectLocation.SelectLocationActivity
import com.xereon.xereon.util.DialogHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {
    private val viewModel by viewModels<LauncherActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.events.observe(this, Observer {
            when (it) {
                LauncherEvent.GoToOnboarding -> {
                    OnboardingActivity.start(this)
                    this.overridePendingTransition(0, 0)
                    finish()
                }
                LauncherEvent.GoToChooseLocation -> {
                    SelectLocationActivity.start(this)
                    this.overridePendingTransition(0, 0)
                    finish()
                }
                LauncherEvent.GoToMainActivity -> {
                    MainActivity.start(this)
                    this.overridePendingTransition(0, 0)
                    finish()
                }
                is LauncherEvent.ShowUpdateDialog -> {
                    showUpdateNeededDialog(it.updateIntent)
                }
            }
        })
    }

    private fun showUpdateNeededDialog(intent: Intent) {
        val updateDialog = DialogHelper.DialogInstance(
            context = this,
            title = "Update verfügbar",
            message = "Bitte beachte, dass Sie Xereon nur mit dem neusten Update verwenden können",
            cancelable = false,
            positiveButton = "Update",
            negativeButton = null,
            positiveButtonFunction = { ContextCompat.startActivity(this, intent, null) }
        )

        DialogHelper.showDialog(updateDialog)

    }

    companion object {
        private val TAG = LauncherActivity::class.simpleName
    }
}