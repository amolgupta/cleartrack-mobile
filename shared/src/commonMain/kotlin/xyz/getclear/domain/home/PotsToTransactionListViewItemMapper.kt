package xyz.getclear.domain.home

import kotlinx.datetime.LocalDate
import xyz.getclear.data.data.Pot
import xyz.getclear.data.net.CurrencyRepository
import xyz.getclear.domain.common.Mapper
import xyz.getclear.domain.details.TransactionListViewItem

class PotsToTransactionListViewItemMapper constructor(
    val currencyRepository: CurrencyRepository,
    private val today: LocalDate
) :
    Mapper<List<Pot>, List<TransactionListViewItem>> {
    override fun invoke(from: List<Pot>): List<TransactionListViewItem> {

        val transactions = mutableListOf<TransactionListViewItem.TransactionListItemEntity>()
        val list = mutableListOf<TransactionListViewItem>()
        from.forEach { pot ->
            transactions.addAll(pot.transactions.map {
                TransactionListViewItem.TransactionListItemEntity(
                    it,
                    pot,
                    currencyRepository.getFxValue(pot.currency),
                    currencyRepository.defaultCurrency()
                )
            }
            )
        }
        transactions
            .sortBy { it.date }
        var isFutureLabelAdded = false
        var total = 0.0f
        transactions.forEach { transaction ->
            if (today < transaction.date!! && !isFutureLabelAdded) {
                list.add(
                    TransactionListViewItem.Label(
                        total, currencyRepository.defaultCurrency().first
                    )
                )
                isFutureLabelAdded = true
            }
            list.add(transaction)
            total += transaction.fxValue
        }
        list.add(
            TransactionListViewItem.Label(
                total,
                currencyRepository.defaultCurrency().first
            )
        )
        return list.reversed()
    }
}