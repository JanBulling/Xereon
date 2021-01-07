package com.xereon.xereon.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.xereon.xereon.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.frg_explore.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainActivityCallback {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.findNavController()

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.exploreFragment, R.id.searchFragment, R.id.favoritesFragment, R.id.mapFragment)
        )

        setSupportActionBar(top_navigation)
        setupActionBarWithNavController(navController, appBarConfiguration)

        bottom_navigation.setupWithNavController(navController)
    }

    override fun setBottomNavBarVisibility(visible: Boolean) {
        bottom_navigation.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun setToolbarVisibility(visible: Boolean) {
        top_navigation.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun setActionBarTitle(title: String) {
        supportActionBar!!.title = title
    }
}