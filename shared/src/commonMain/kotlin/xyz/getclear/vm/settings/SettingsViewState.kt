package xyz.getclear.vm.settings

import xyz.getclear.data.data.Currencies

sealed class SettingsViewState {
    data class Error(val error: String) : SettingsViewState()
    data class Data(
        val currencies: Currencies = emptyMap(),
        val username: String? = null,
        val email: String? = null,
        val image: String? = null,
        val subscriptionEndDate: String? = null
    ) : SettingsViewState()
    object RestartEvent : SettingsViewState()
}