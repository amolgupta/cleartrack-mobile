package xyz.getclear.android.details

import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import xyz.getclear.android.R
import xyz.getclear.domain.details.TransactionListViewItem

class MiniTransactionListAdapter(
    val view: LinearLayoutCompat,
    private var items: List<TransactionListViewItem.TransactionListItemEntity>,
    private val onItemDeleted: (id: String) -> Unit
) {

    init {
        view.removeAllViews()
        for (i in 0..minOf(7, items.size - 1)) {
            val itemView = LayoutInflater.from(view.context)
                .inflate(R.layout.item_transaction, view, false)
            TransactionsAdapter.TransactionItemViewHolder(itemView).also {
                it.bind(items[i]){
                    items[i].id?.let { onItemDeleted(it) }
                }
                view.addView(itemView)
            }
        }
    }
}