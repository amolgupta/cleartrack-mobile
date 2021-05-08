package xyz.getclear.domain.home

import kotlinx.datetime.*
import xyz.getclear.data.data.DateRange
import xyz.getclear.data.data.Pot
import xyz.getclear.data.net.CurrencyRepository
import xyz.getclear.domain.reports.Entry

open class ChartMapper(
    private val currencyRepository: CurrencyRepository,
    private val today: LocalDate
) {
    fun getData(pot: Pot, dateRange: DateRange) = getData(listOf(pot), dateRange)

    fun getData(pots: List<Pot>, dateRange: DateRange) =
        pots.getTransactions()
            .toTimeSeries()
            .filter(today.minus(dateRange.days, DateTimeUnit.DAY), today)
            .addBoundaryValues(dateRange)

    private fun List<Entry>.toTimeSeries(): List<Entry> {
        val entries = mutableListOf<Entry>()
        var currentValue = 0.0f

        sortedBy { it.x }.forEach { its ->
            currentValue += its.y
            entries.add(Entry(its.x, currentValue))
        }

        return entries
    }

    private fun List<Entry>.addBoundaryValues(dateRange: DateRange): MutableList<Entry> {
        return this.toMutableList().apply {
            findLast { it.x < today }?.y?.let {
                add(Entry(today, it))
            }
            val start = today.minus(DatePeriod(days = dateRange.days))
            findLast { it.x < start }?.y?.let {
                add(Entry(start, it))
            }
        }
    }

    private fun List<Entry>.filter(startDate: LocalDate, endDate: LocalDate): List<Entry> {
        return filter {
            it.x > startDate && it.x < endDate
        }
    }

    private fun List<Pot>.getTransactions(): List<Entry> {
        return flatMap { pot ->
            val fx = currencyRepository.getFxValue(pot.currency)
            pot.transactions.map {
                Entry(it.date!!, it.amount * fx)
            }
        }
    }
}
