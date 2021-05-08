package xyz.cleartrack.shared

import platform.Foundation.NSUserDefaults
import xyz.getclear.data.data.DEFAULT_CURRENCY
import xyz.getclear.data.net.BASE_CURRENCY
import xyz.getclear.data.net.DefaultCurrencyProvider

class DefaultCurrencyProviderImpl constructor(
    private val preferences: NSUserDefaults
) : DefaultCurrencyProvider {
    override fun getDefaultCurrency() =
        if (preferences.stringForKey(BASE_CURRENCY) != null) {
            preferences.stringForKey(BASE_CURRENCY)!!
        } else {
            DEFAULT_CURRENCY
        }
}