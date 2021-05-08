package xyz.getclear.android.settings

interface SettingsView {
    fun displayCurrencies(currencies: Map<String, Float>)
    fun displayUsername(name: String)
    fun displayEmailId(email: String)
    fun displaySubscriptionEndDate(date: String)
    fun displayUserImage(url: String)
    fun restartActivity()
}
