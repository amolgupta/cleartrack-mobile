package xyz.getclear.domain.details

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayAt
import xyz.getclear.data.data.Currency
import xyz.getclear.data.data.Pot
import xyz.getclear.data.data.Transaction

sealed class TransactionListViewItem {
    class Label(val value: Float, val currency: String) : TransactionListViewItem()

    class TransactionListItemEntity constructor(
        transaction: Transaction,
        pot: Pot,
        fx: Float,
        val defaultCurrency: Currency
    ) : TransactionListViewItem() {
        val id: String? = transaction.id
        val amount: Float = transaction.amount
        val date = transaction.date
        val potName = pot.name
        val currency = pot.currency
        val fxValue = transaction.amount * fx
        val isFuture = Clock.System.todayAt(TimeZone.currentSystemDefault()) < date!!
    }
}