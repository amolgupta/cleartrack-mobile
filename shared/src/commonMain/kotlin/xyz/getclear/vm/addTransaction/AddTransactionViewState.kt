package xyz.getclear.vm.addTransaction

import kotlinx.datetime.LocalDate

sealed class AddTransactionViewState {
    class Error(val message: String) : AddTransactionViewState()
    object Loading : AddTransactionViewState()
    object Complete : AddTransactionViewState()

    class Data(
        val title: String,
        val balance: Float,
        val date: LocalDate
    ) : AddTransactionViewState()
}