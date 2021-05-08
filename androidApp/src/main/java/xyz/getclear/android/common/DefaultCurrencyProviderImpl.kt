package xyz.getclear.android.common

import android.content.SharedPreferences
import xyz.getclear.data.data.DEFAULT_CURRENCY
import xyz.getclear.data.net.BASE_CURRENCY
import xyz.getclear.data.net.DefaultCurrencyProvider

class DefaultCurrencyProviderImpl constructor(
    private val sharedPreferences: SharedPreferences
) : DefaultCurrencyProvider {
    override fun getDefaultCurrency() =
        sharedPreferences.getString(BASE_CURRENCY, DEFAULT_CURRENCY)!!
}