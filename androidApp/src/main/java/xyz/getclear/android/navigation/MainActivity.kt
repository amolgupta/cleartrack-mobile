package xyz.getclear.android.navigation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import xyz.getclear.android.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.ext.android.inject
import xyz.getclear.android.common.BaseActivity
import xyz.getclear.android.login.AuthActivity
import xyz.getclear.data.net.contract.TokenManager

class MainActivity : BaseActivity() {

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }
    private val navView by lazy { findViewById<BottomNavigationView>(R.id.navigation_bar) }

    private val tokenManager: TokenManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        validateLogin()

        setContentView(R.layout.activity_main)

        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.menu_pots, R.id.menu_transactions, R.id.menu_preferences, R.id.menu_reports -> {
                    showBottomNavigation()
                }
                else -> {
                    hideBottomNavigation()
                }
            }
        }
    }


    private fun hideBottomNavigation() {
        with(navView) {
            if (visibility == View.VISIBLE && alpha == 1f) {
                animate()
                    .alpha(0f)
                    .withEndAction { visibility = View.GONE }
            }
        }
    }

    private fun showBottomNavigation() {
        with(navView) {
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
        }
    }

    private fun validateLogin() {
        if (tokenManager.getToken() == null) {
            val loginIntent = Intent(this, AuthActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
    }
}
