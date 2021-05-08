package xyz.getclear.data.net

import xyz.getclear.data.data.Currencies
import xyz.getclear.data.data.Currency
import xyz.getclear.data.data.DEFAULT_CURRENCY
import xyz.getclear.data.net.contract.TokenManager

class CurrencyRepository constructor(
    private val currencyApi: CurrencyApi,
    private val tokenManager: TokenManager,
    private val currencyProvider: DefaultCurrencyProvider
) {
    var currencies: Currencies = mapOf()

    fun defaultCurrency(): Currency {
        currencyProvider.getDefaultCurrency().let { name ->
            return if (currencies[name] != null) {
                Pair(name, currencies.getValue(name))
            } else {
                Pair(DEFAULT_CURRENCY, 1.0f)
            }
        }
    }

    suspend fun getCurrencies(): Currencies {
        if (currencies.isEmpty()) {
            val response = kotlin.runCatching {
                currencyApi.getCurrencies(tokenManager.getToken()!!.key)
            }.getOrElse { throw it }

            currencies =
                response.mapKeys { its -> its.key.substring(3, 6) }
        }
        return currencies
    }

    fun getFxValue(currency: String) =
        (1.0f / currencies.getValue(currency)) * currencies.getValue(defaultCurrency().first)


    fun getUSDValue(amount: Float) =
        amount * currencies.getValue(defaultCurrency().first)

}

interface DefaultCurrencyProvider {
    fun getDefaultCurrency(): String
}