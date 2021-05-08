package xyz.getclear.data.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayAt
import xyz.getclear.data.data.Pot

fun Pot.currentBalance() = balance(Clock.System.todayAt(TimeZone.currentSystemDefault()))

fun Pot.balance(date: LocalDate?): Float {
    var sum = 0.0f
    transactions.forEach { transaction ->
        transaction.date?.let { transactionDate ->
           if (date == null || transactionDate < date) {
                sum += transaction.amount
            }
        }
    }
    return sum
}