package xyz.getclear.vm.details

import xyz.getclear.domain.details.TransactionListViewItem
import xyz.getclear.domain.reports.Entry

class DetailsViewState(
    val title: String,
    val data: List<TransactionListViewItem>,
    val tags: List<String>,
    val entries: List<Entry>?
)

sealed class DetailsEvents {
    class Error(val error: String) : DetailsEvents()
    object Deleted : DetailsEvents()
}