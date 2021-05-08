package xyz.getclear.domain.home

import xyz.getclear.data.data.Pot
import xyz.getclear.data.net.CurrencyRepository
import xyz.getclear.domain.common.Mapper


@Suppress("EXPERIMENTAL_API_USAGE")
class TrackedCurrenciesMapper constructor(val currencyRepository: CurrencyRepository) :
    Mapper<List<Pot>, Map<String, Float>> {
    override fun invoke(from: List<Pot>): Map<String, Float> {
        val output = mutableMapOf<String, Float>()

        from.distinctBy { pot -> pot.currency }
            .forEach { pot ->
                if (pot.currency != currencyRepository.defaultCurrency().first) {
                    output[pot.currency] = currencyRepository.getFxValue(pot.currency)
                }
            }
        return output
    }
}