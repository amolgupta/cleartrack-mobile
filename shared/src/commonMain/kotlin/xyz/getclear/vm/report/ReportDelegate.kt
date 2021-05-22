package xyz.getclear.vm.report

import xyz.getclear.data.data.Pot
import xyz.getclear.domain.common.Mapper
import xyz.getclear.domain.reports.BarEntry
import xyz.getclear.domain.reports.Entry
import xyz.getclear.domain.reports.mappers.GrowthReportMapper
import xyz.getclear.domain.reports.mappers.TrackedCurrenciesReportMapper

data class ReportState(
    val reports : List<ReportData> = emptyList(),
)

sealed class ReportData {
    data class GrowthReport(val entries: List<Entry>) : ReportData()
    data class CurrencyReport(val entries: List<BarEntry>) : ReportData()
}

abstract class ReportAdapter<T : ReportData>(val mapper: Mapper<List<Pot>, T>) {
    fun getReport(pots: List<Pot>): T {
        return mapper.invoke(pots)
    }
}

class GrowthReportAdapter constructor(growthReportMapper: GrowthReportMapper) : ReportAdapter<ReportData.GrowthReport>(growthReportMapper)
class CurrencyReportAdapter constructor(currenciesReportMapper: TrackedCurrenciesReportMapper) : ReportAdapter<ReportData.CurrencyReport>(currenciesReportMapper)

