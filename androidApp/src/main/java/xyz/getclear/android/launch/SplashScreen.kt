package xyz.getclear.android.launch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import xyz.getclear.android.R
import com.onesignal.OneSignal
import org.koin.androidx.viewmodel.ext.android.viewModel
import xyz.getclear.android.common.BaseActivity
import xyz.getclear.android.login.AuthActivity
import xyz.getclear.android.navigation.MainActivity
import xyz.getclear.vm.appStart.AppStartViewModel
import xyz.getclear.vm.appStart.AppStartViewState

class SplashActivity : BaseActivity(), AppStartView {

    private val model: AppStartViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        model.start()
        model.networkError.addObserver {
            it?.let {  showError("Could not connect to servers. Please verify your internet connection and try again") }
        }
        model.viewState.addObserver { state ->
            when (state) {
                is AppStartViewState.Loading -> {
                }
                is AppStartViewState.Error -> {
                    showError(state.error)
                }
                is AppStartViewState.Success -> {
                    launchMainActivity()
                }
                is AppStartViewState.AuthError -> {
                    launchLoginActivity()
                }
                is AppStartViewState.UpdateRequired -> {
                    updateDialog()
                }
            }
        }
        initOneSignal()
    }

    private fun initOneSignal() {
        OneSignal.startInit(applicationContext)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
    }

    override fun launchMainActivity() {
        val homeIntent = Intent(this, MainActivity::class.java)
        startActivity(homeIntent)
        finish()
    }

    override fun launchLoginActivity() {
        val homeIntent = Intent(this, AuthActivity::class.java)
        startActivity(homeIntent)
        finish()
    }

    private fun updateDialog() {
        AlertDialog.Builder(
            ContextThemeWrapper(
                this,
                R.style.AppTheme_Dialog
            )
        ).setCancelable(false)
            .setTitle(getString(R.string.update_app_title))
            .setPositiveButton(getString(R.string.update_cta)) { view, _ ->
                view.dismiss()

            }
            .setMessage(getString(R.string.update_app_message)).show()
    }

    private fun showError(message: String) {
        AlertDialog.Builder(
            ContextThemeWrapper(
                this,
                R.style.AppTheme_Dialog
            )
        ).setTitle("Error")
            .setPositiveButton("Ok") { view, _ ->
                view.dismiss()
                finish()
            }
            .setMessage(message).show()
    }
}