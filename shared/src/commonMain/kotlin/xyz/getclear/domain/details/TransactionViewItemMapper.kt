package xyz.getclear.domain.details

import kotlinx.datetime.LocalDate
import xyz.getclear.data.data.Pot
import xyz.getclear.data.net.CurrencyRepository
import xyz.getclear.domain.common.Mapper

class TransactionViewItemMapper constructor(
    val currencyRepository: CurrencyRepository,
    private val today : LocalDate) :
    Mapper<Pot, List<TransactionListViewItem>> {
    override fun invoke(from: Pot): List<TransactionListViewItem> {

        val list = mutableListOf<TransactionListViewItem>()
        var isFutureLabelAdded = false
        var total = 0.0f
        val transactions = from.transactions.sortedBy { it.date }


        transactions.forEach { transaction ->
            if (today < transaction.date!! && !isFutureLabelAdded) {
                list.add(
                    TransactionListViewItem.Label(
                        total,
                        from.currency
                    )
                )
                isFutureLabelAdded = true
            }
            list.add(
                TransactionListViewItem.TransactionListItemEntity(
                    transaction,
                    from,
                    currencyRepository.getFxValue(from.currency),
                    currencyRepository.defaultCurrency()
                )
            )
            total += transaction.amount
        }

        list.add(
            TransactionListViewItem.Label(
                total,
                from.currency
            )
        )
        return list.reversed()
    }
}