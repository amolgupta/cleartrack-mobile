package xyz.getclear.android.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.view.ContextThemeWrapper
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import xyz.getclear.android.R
import xyz.getclear.android.common.BaseActivity
import xyz.getclear.android.data.BASE_URL
import xyz.getclear.android.databinding.ActivityLoginBinding
import xyz.getclear.android.navigation.MainActivity
import xyz.getclear.vm.auth.AuthCommand
import xyz.getclear.vm.auth.AuthViewModel
import xyz.getclear.vm.auth.AuthViewState
import xyz.getclear.vm.auth.PageViewType
import kotlinx.coroutines.flow.collect

const val TERMS_URL = BASE_URL + "terms/"

class AuthActivity : BaseActivity() {

    private val model: AuthViewModel by inject()

    private var binding: ActivityLoginBinding? = null
    private var uiStateJob: Job? = null

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        binding?.run {
            forgotPassword.movementMethod = LinkMovementMethod.getInstance()
            txtLogin.setOnClickListener {
                model.dispatch(AuthCommand.ViewType(PageViewType.LOGIN))
            }

            txtRegister.setOnClickListener {
                model.dispatch(AuthCommand.ViewType(PageViewType.REGISTER))
            }

            inputEmail.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (inputEmail.right - inputEmail.compoundDrawablesRelative[2].bounds.width())) {
                        model.dispatch(AuthCommand.WhyEmail)
                        return@setOnTouchListener true
                    }
                }
                view.performClick()
                false
            }

            txtTnc.setOnClickListener { model.dispatch(AuthCommand.TermsAndConditions) }
        }
        uiStateJob = lifecycleScope.launch {

            model.viewState.collect { state ->
                when (state) {
                    is AuthViewState.Success -> {
                        navigateToMainActivity()
                    }
                    is AuthViewState.Error -> {
                        resetErrors()
                        showError(state)
                    }
                    is AuthViewState.EmailDialog ->
                        showEmailDialog()
                    is AuthViewState.TermsAndConditions ->
                        openTermsAndConditions()
                    is AuthViewState.ViewType -> {
                        resetErrors()
                        setFormState(state.viewType)
                    }
                }
            }
        }
    }


    override fun onStop() {
        uiStateJob?.cancel()
        super.onStop()
    }

    @SuppressLint("CheckResult")
    fun loginClicked(view: View) {
        binding?.run {
            model.dispatch(
                AuthCommand.Submit(
                    username = edtUsername.editText?.text.toString(),
                    email = edtEmail.editText?.text.toString(),
                    password = edtPassword.editText?.text.toString(),
                    acceptTerms = checkboxRegister.isChecked
                )
            )
        }
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    private fun resetErrors() {
        binding?.run {
            edtPassword.error = null
            edtUsername.error = null
            edtEmail.error = null
            checkboxRegister.error = null
        }
    }

    private fun showError(error: AuthViewState.Error) {
        binding?.run {
            if (error.error != null) {
                Snackbar.make(
                    findViewById(R.id.layout_login_parent),
                    error.error.toString(),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            if (error.passwordError != null) {
                edtPassword.error = error.passwordError
            }
            if (error.usernameError != null) {
                edtUsername.error = error.usernameError
            }
            if (error.emailError != null) {
                edtEmail.error = error.emailError
            }
            if (error.checkBoxError) {
                checkboxRegister.error = "Please accept terms"
            }
        }
    }

    private fun navigateToMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun setFormState(state: PageViewType) {
        binding?.run {
            edtUsername.requestFocus()
            if (state == PageViewType.LOGIN) {
                txtLogin.visibility = View.GONE
                txtRegister.visibility = View.VISIBLE
                forgotPassword.visibility = View.VISIBLE
                edtEmail.visibility = View.GONE
                layoutTnc.visibility = View.GONE

            } else {
                txtLogin.visibility = View.VISIBLE
                txtRegister.visibility = View.GONE
                edtEmail.visibility = View.VISIBLE
                layoutTnc.visibility = View.VISIBLE
                forgotPassword.visibility = View.GONE
            }
        }
    }

    private fun showEmailDialog() {
        androidx.appcompat.app.AlertDialog.Builder(
            ContextThemeWrapper(
                this,
                R.style.AppTheme_Dialog
            )
        ).setTitle("Why is email optional.")
            .setPositiveButton("Ok") { view, _ ->
                view.dismiss()
            }
            .setMessage(getString(R.string.email_policy)).show()
    }

    private fun openTermsAndConditions() {
        CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(TERMS_URL))
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
