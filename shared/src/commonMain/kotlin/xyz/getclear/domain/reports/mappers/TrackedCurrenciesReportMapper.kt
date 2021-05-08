package xyz.getclear.domain.reports.mappers

import xyz.getclear.data.data.Pot
import xyz.getclear.data.net.CurrencyRepository
import xyz.getclear.data.utils.currentBalance
import xyz.getclear.domain.common.Mapper
import xyz.getclear.domain.reports.BarEntry

class TrackedCurrenciesReportMapper constructor(
    private val currencyRepository: CurrencyRepository
) : Mapper<List<Pot>, List<BarEntry>> {

    override fun invoke(from: List<Pot>): List<BarEntry> {
        val currencyList = currencyList(from)
        var i = 0

        return currencyList.map { currency ->
            var baseSum =  0.0f
            var defaultSum = 0.0f
            val fx = currencyRepository.getFxValue(currency)
            from.filter { it.currency == currency }
                .forEach {
                    baseSum += it.currentBalance()
                    defaultSum += it.currentBalance() * fx
                }
            BarEntry(
                i++ + 1f,
                defaultSum,
                "$baseSum  $currency"
            )
        }
    }

    private fun currencyList(data: List<Pot>) =
        data.distinctBy { it.currency }.map { it.currency }.sortedBy { it }
}