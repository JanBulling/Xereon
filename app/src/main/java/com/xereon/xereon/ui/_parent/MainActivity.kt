package com.xereon.xereon.ui._parent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainActivityCallback {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_Material)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.findNavController()

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.exploreFragment, R.id.searchFragment, R.id.favoritesFragment, R.id.mapFragment)
        )

        setSupportActionBar(top_navigation)
        setupActionBarWithNavController(navController, appBarConfiguration)

        bottom_navigation.setupWithNavController(navController)
        bottom_navigation.setOnNavigationItemReselectedListener { /*NO-OP*/ }

        navController.addOnDestinationChangedListener {_, destination, _ ->
            when (destination.id) {
                R.id.exploreFragment, R.id.favoritesFragment, R.id.mapFragment -> {
                    bottom_navigation.visibility = View.VISIBLE
                    top_navigation_search_input.visibility = View.GONE
                }
                R.id.searchFragment -> {
                    bottom_navigation.visibility = View.VISIBLE
                    top_navigation_search_input.visibility = View.VISIBLE
                }
                else -> {
                    bottom_navigation.visibility = View.GONE
                    top_navigation_search_input.visibility = View.GONE
                }
            }
        }
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container)
        val fragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment
        if (fragment is OnBackPressedListener) {
            if (fragment.onBackPressed())
                return
        }
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun setActionBarTitle(title: String) {
        supportActionBar!!.title = title
    }

    fun getSearch() = top_navigation_search_input
}