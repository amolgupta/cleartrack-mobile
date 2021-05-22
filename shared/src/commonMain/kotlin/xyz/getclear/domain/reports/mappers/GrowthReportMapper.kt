package xyz.getclear.domain.reports.mappers

import kotlinx.datetime.*
import xyz.getclear.data.data.Pot
import xyz.getclear.data.net.CurrencyRepository
import xyz.getclear.domain.common.Mapper
import xyz.getclear.domain.reports.Entry
import xyz.getclear.vm.report.ReportData

class GrowthReportMapper constructor(val currencyRepository: CurrencyRepository) :
    Mapper<List<Pot>, ReportData.GrowthReport> {

    override fun invoke(from: List<Pot>): ReportData.GrowthReport {
        val output = mutableMapOf<LocalDate, Float>()
        from.forEach { pot ->
            val fx = currencyRepository.getFxValue(pot.currency)
            pot.transactions.forEach {
                it.date?.let { date ->
                    val startOfMonth = getStartOfMonth(date)
                    if (!output.containsKey(startOfMonth)) {
                        output[startOfMonth] = it.amount * fx
                    } else {
                        output[startOfMonth] = output[startOfMonth]!! + (it.amount * fx)
                    }
                }
            }
        }
        return ReportData.GrowthReport(
            output.map { Entry(it.key, it.value) }.sortedBy { it.x }.drop(1)
        )
    }

    private fun getStartOfMonth(date: LocalDate): LocalDate {
        return date.minus(date.dayOfMonth, DateTimeUnit.DAY)
    }
}