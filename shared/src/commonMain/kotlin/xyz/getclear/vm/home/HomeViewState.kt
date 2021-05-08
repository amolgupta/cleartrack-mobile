package xyz.getclear.vm.home

import xyz.getclear.domain.details.TransactionListViewItem
import xyz.getclear.domain.home.BalanceUiModel
import xyz.getclear.domain.reports.Entry

sealed class HomeViewState {
    data class Error(val error: String) : HomeViewState()
    object Loading : HomeViewState()
    data class Data(
        val data: List<TransactionListViewItem>,
        val trackedCurrencies: Map<String, Float> = mapOf(),
        val balance : BalanceUiModel,
        val chartEntries: List<Entry>?
    ) : HomeViewState()

    object NoTransactions : HomeViewState()
    object NoPots : HomeViewState()
}

sealed class HomeCommand {
    object Start : HomeCommand()
    data class SetStartDate(val tag: String) : HomeCommand()
    data class DeleteTransaction(val id: String) : HomeCommand()
}

