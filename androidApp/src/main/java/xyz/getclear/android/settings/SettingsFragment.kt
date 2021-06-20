package xyz.getclear.android.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import xyz.getclear.android.R
import xyz.getclear.android.data.BASE_URL
import com.google.android.material.snackbar.Snackbar
import com.onesignal.OneSignal
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import xyz.getclear.android.common.CircleTransform
import xyz.getclear.android.common.SETTINGS_PREF_NAME
import xyz.getclear.android.design.themeColor
import xyz.getclear.data.data.DEFAULT_CURRENCY
import xyz.getclear.data.net.BASE_CURRENCY
import xyz.getclear.data.net.SHOW_NOTIFICATIONS
import xyz.getclear.data.net.USERNAME
import xyz.getclear.domain.common.*
import xyz.getclear.vm.settings.SettingsViewModel
import xyz.getclear.vm.settings.SettingsViewState

/**
 * TODO: Refactor to move logic to VM
 * see https://stackoverflow.com/questions/50649014/livedata-with-shared-preferences
 */
class SettingsFragment : PreferenceFragmentCompat(), SettingsView,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val analyticsWrapper: AnalyticsWrapper by inject()

    private val imageLoader: Picasso by inject()
    private val model: SettingsViewModel by inject()

    private var usernamePreference: Preference? = null
    private var subscription: Preference? = null
    private var uiStateJob: Job? = null

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        if (key == BASE_CURRENCY) {
            analyticsWrapper.logEvent(EVENT_CURRENCY_CHANGE)
            findPreference<Preference>(BASE_CURRENCY)?.summary =
                sharedPreferences.getString(BASE_CURRENCY, DEFAULT_CURRENCY)
            restartActivity()
        } else if (key == SHOW_NOTIFICATIONS) {
            OneSignal.disablePush(!sharedPreferences.getBoolean(SHOW_NOTIFICATIONS, true))
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = SETTINGS_PREF_NAME
        addPreferencesFromResource(R.xml.app_preferences)
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        (findPreference<Preference>(SHOW_NOTIFICATIONS) as CheckBoxPreference).apply {
            isChecked =
                preferenceManager.sharedPreferences.getBoolean(SHOW_NOTIFICATIONS, false)
        }
        findPreference<Preference>(BASE_CURRENCY)?.apply {
            summary =
                preferenceManager.sharedPreferences.getString(BASE_CURRENCY, DEFAULT_CURRENCY)
        }
        usernamePreference = findPreference(USERNAME)
        subscription = findPreference(SUBSCRIPTION)

        findPreference<Preference>(INVITE)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "I am using this app to track my your accounts. Do try it out. https://play.google.com/store/apps/details?id=io.boost.app"
                    )
                    type = "text/plain"
                }
                analyticsWrapper.logEvent(EVENT_SHARE)
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
                true
            }
        }
        findPreference<Preference>(LOGOUT)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                model.logout()
                analyticsWrapper.logEvent(EVENT_LOGOUT)
                true
            }
        }
        findPreference<Preference>(RESET_PASSWORD)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                openLink(RESET_PASSWORD_URL)
                true
            }
        }
        findPreference<Preference>(TERMS_OF_USE)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                openLink(TERMS_URL)
                true
            }
        }
        findPreference<Preference>(PRIVACY_POLICY)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                openLink(PRIVACY_POLICY_URL)
                true
            }
        }

        for (i in 0 until preferenceScreen.preferenceCount) {
            preferenceScreen.getPreference(i)
                .icon.setTint(requireActivity().themeColor(R.attr.iconColor))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.start()
        uiStateJob = lifecycleScope.launch {

            model.viewState.collect { state ->
                when (state) {

                    is SettingsViewState.Data -> {
                        displayCurrencies(state.currencies)
                        state.email?.let { displayEmailId(it) }
                        state.username?.let { displayUsername(it) }
                        state.image?.let { displayUserImage(it) }
                        state.subscriptionEndDate?.let { displaySubscriptionEndDate(it) }
                    }
                    is SettingsViewState.Error -> {
                        showError(state.error)
                    }
                    is SettingsViewState.RestartEvent -> {
                        restartActivity()
                    }
                }
            }
        }
    }

    override fun displayCurrencies(currencies: Map<String, Float>) {
        findPreference<ListPreference>(BASE_CURRENCY)?.apply {
            entries = currencies.keys.toTypedArray()
            entryValues = currencies.keys.toTypedArray()
            summary = entry
        }
    }


    private fun showError(message: String) {
        view?.let {
            Snackbar.make(
                it.findViewById(R.id.settings_container),
                message, Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun finish() {
        activity?.finish()
    }

    override fun displayUsername(name: String) {
        usernamePreference?.title = name
    }

    override fun displayEmailId(email: String) {
        usernamePreference?.summary = email
    }

    override fun displaySubscriptionEndDate(date: String) {
        subscription?.summary = "ends %s".format(date)
    }

    @SuppressLint("CheckResult")
    override fun displayUserImage(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = imageLoader.load(url)

            withContext(Dispatchers.Main) {
                try {
                    usernamePreference?.icon = BitmapDrawable(
                        resources, bitmap.resize(96, 96)
                            .transform(CircleTransform()).get()
                    )
                    analyticsWrapper.logEvent(EVENT_GRAVATAR_LOADED)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun openLink(string: String) {
        CustomTabsIntent.Builder().build().launchUrl(requireContext(), Uri.parse(string))
    }

    override fun restartActivity() {
        if (isAdded) {
            context?.packageName?.let {
                context?.packageManager?.getLaunchIntentForPackage(
                    it
                )
            }?.apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }.also {
                startActivity(it)
                finish()
            }
        }
    }
    override fun onStop() {
        uiStateJob?.cancel()
        super.onStop()
    }

    companion object {

        const val INVITE = "invite"
        const val LOGOUT = "logout"
        const val TERMS_OF_USE = "terms_and_conditions"
        const val TERMS_URL = BASE_URL + "terms/"
        const val PRIVACY_POLICY = "privacy_policy"
        const val PRIVACY_POLICY_URL = BASE_URL + "policy/"
        const val RESET_PASSWORD = "reset_password"
        const val RESET_PASSWORD_URL = BASE_URL + "accounts/password/reset/"
        const val SUBSCRIPTION = "subscription"
    }
}