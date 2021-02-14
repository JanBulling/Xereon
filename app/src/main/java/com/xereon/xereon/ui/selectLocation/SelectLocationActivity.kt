package com.xereon.xereon.ui.selectLocation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.xereon.xereon.R
import com.xereon.xereon.data.location.Place
import com.xereon.xereon.data.location.IPLocation
import com.xereon.xereon.storage.LocalData
import com.xereon.xereon.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectLocationActivity : AppCompatActivity() {
    companion object {
        private val TAG: String? = SelectLocationActivity::class.simpleName

        fun start(context: Context) {
            val intent = Intent(context, SelectLocationActivity::class.java)
            context.startActivity(intent)
        }
    }

    @Inject lateinit var localData: LocalData

    private val FragmentManager.currentNavigationFragment: Fragment?
        get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        supportFragmentManager.currentNavigationFragment?.onActivityResult(
            requestCode,
            resultCode,
            data
        )
    }

    fun completeSelectLocation(data: IPLocation) {
        localData.setLocationData(data)
        completeSelectLocation()
    }

    fun completeSelectLocation(data: Place) {
        localData.setLocationData(data)
        completeSelectLocation()
    }

    private fun completeSelectLocation() {
        localData.setLocationSet(true)
        MainActivity.start(this)
        finish()
    }

    fun goBack() {
        onBackPressed()
    }
}