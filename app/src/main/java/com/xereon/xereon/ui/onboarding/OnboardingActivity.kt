package com.xereon.xereon.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleObserver
import com.xereon.xereon.R
import com.xereon.xereon.data.login.LoginResponse
import com.xereon.xereon.storage.LocalData
import com.xereon.xereon.ui.selectLocation.SelectLocationActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingActivity() : AppCompatActivity(), LifecycleObserver {
    companion object {
        private val TAG: String? = OnboardingActivity::class.simpleName

        fun start(context: Context) {
            val intent = Intent(context, OnboardingActivity::class.java)
            context.startActivity(intent)
        }
    }

    @Inject lateinit var localData: LocalData

    private val FragmentManager.currentNavigationFragment: Fragment?
        get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        supportFragmentManager.currentNavigationFragment?.onActivityResult(
            requestCode,
            resultCode,
            data
        )
    }

    fun completeOnboarding(loginResponse: LoginResponse? = null) {
        localData.setOnborded(true)
        if (loginResponse != null)
            localData.setLoginData(loginResponse)
        SelectLocationActivity.start(this)
        finish()
    }

    fun goBack() {
        onBackPressed()
    }

}