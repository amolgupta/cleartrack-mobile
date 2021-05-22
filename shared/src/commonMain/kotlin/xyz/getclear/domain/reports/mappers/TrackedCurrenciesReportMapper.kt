package xyz.getclear.domain.reports.mappers

import xyz.getclear.data.data.Pot
import xyz.getclear.data.net.CurrencyRepository
import xyz.getclear.data.utils.currentBalance
import xyz.getclear.domain.common.Mapper
import xyz.getclear.domain.reports.BarEntry
import xyz.getclear.vm.report.ReportData

class TrackedCurrenciesReportMapper constructor(
    private val currencyRepository: CurrencyRepository
) : Mapper<List<Pot>, ReportData.CurrencyReport> {

    override fun invoke(from: List<Pot>): ReportData.CurrencyReport {
        val currencyList = currencyList(from)
        var i = 0

        val entries = currencyList.map { currency ->
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
        return ReportData.CurrencyReport(entries)
    }

    private fun currencyList(data: List<Pot>) =
        data.distinctBy { it.currency }.map { it.currency }.sortedBy { it }
}