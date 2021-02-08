package com.xereon.xereon.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.xereon.xereon.R
import com.xereon.xereon.di.ProvideApplicationState
import com.xereon.xereon.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainActivityCallback {
    private lateinit var navController: NavController

    @Inject @ProvideApplicationState lateinit var applicationState: Constants.ApplicationState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_Material)
        setContentView(R.layout.activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.exploreFragment,
                R.id.searchFragment,
                R.id.favoritesFragment,
                R.id.mapFragment
            )
        )

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)

        when(applicationState) {
            Constants.ApplicationState.FIRST_OPENED ->
                graph.startDestination = R.id.chooseLoginOrSignUpFragment

            Constants.ApplicationState.LOGGED_IN_NO_LOCATION_VALID,
            Constants.ApplicationState.LOGGED_IN_NO_LOCATION_NOT_VALID,
            Constants.ApplicationState.SKIPPED_NO_LOCATION ->
                graph.startDestination = R.id.chooseLocationFragment

            Constants.ApplicationState.SKIPPED_HAS_LOCATION,
            Constants.ApplicationState.LOGGED_IN_HAS_LOCATION_NOT_VALID,
            Constants.ApplicationState.VALID_USER_ACCOUNT ->
                graph.startDestination = R.id.exploreFragment
        }

        navController = navHostFragment.navController
        navController.setGraph(graph, intent.extras)

        setSupportActionBar(top_navigation)
        setupActionBarWithNavController(navController, appBarConfiguration)

        bottom_navigation.setupWithNavController(navController)
        bottom_navigation.setOnNavigationItemReselectedListener { /*NO-OP*/ }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.exploreFragment, R.id.favoritesFragment, R.id.mapFragment -> {
                    bottom_navigation.visibility = View.VISIBLE
                    top_navigation_search_input.visibility = View.GONE
                    top_navigation.visibility = View.VISIBLE
                }
                R.id.searchFragment -> {
                    bottom_navigation.visibility = View.VISIBLE
                    top_navigation_search_input.visibility = View.VISIBLE
                    top_navigation.visibility = View.VISIBLE
                }
                R.id.loginFragment, R.id.signUpFragment, R.id.chooseLoginOrSignUpFragment,
                R.id.chooseLocationFragment -> {
                    bottom_navigation.visibility = View.GONE
                    top_navigation.visibility = View.GONE
                }
                else -> {
                    bottom_navigation.visibility = View.GONE
                    top_navigation_search_input.visibility = View.GONE
                    top_navigation.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container)
        val fragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment
        if (fragment is OnBackPressedListener && fragment.onBackPressed())
            return
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