package xyz.getclear.domain.transactions

import kotlinx.datetime.*
import xyz.getclear.data.data.Transaction
import xyz.getclear.domain.common.Mapper
import xyz.getclear.domain.reports.Entry

class TransactionsToEntriesMapper constructor(
    private val today: LocalDate
) : Mapper<List<Transaction>, List<Entry>> {
    override fun invoke(from: List<Transaction>): List<Entry> {
        val entries = mutableListOf<Entry>()
        val transactions = from.sortedBy {
            it.date
        }

        if (transactions.isNotEmpty()) {
            var currentValue = 0.0f
            for (transaction in transactions) {
                currentValue += transaction.amount
                entries.add(Entry(transaction.date!!, currentValue))
            }
            entries.add(
                Entry(
                    (transactions.last().date?.let { today })!!,
                    currentValue
                )
            )
        }
        return entries
    }
}