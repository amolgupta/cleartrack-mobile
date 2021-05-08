package xyz.getclear.android.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import xyz.getclear.android.R
import xyz.getclear.android.design.NumberView
import xyz.getclear.domain.details.TransactionListViewItem
import java.util.*

class TransactionsAdapter(
    private var items: List<TransactionListViewItem>,
    val onItemDeleted: (id: String) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val DATA_ITEM = 101
        const val LABEL_ITEM = 102
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == DATA_ITEM) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_transaction, parent, false)
            TransactionItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_balance_label, parent, false)
            LabelViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int) =
        if (items[position] is TransactionListViewItem.TransactionListItemEntity) {
            DATA_ITEM
        } else {
            LABEL_ITEM
        }


    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            LABEL_ITEM -> {
                val item = items[position] as TransactionListViewItem.Label
                holder as LabelViewHolder
                holder.label.setNumber(item.value, item.currency)
            }
            DATA_ITEM -> {
                val item = items[position] as TransactionListViewItem.TransactionListItemEntity
                holder as TransactionItemViewHolder
                holder.bind(item) {
                    item.id?.let { onItemDeleted(it) }
                }
            }
        }
    }

    fun submitList(transactions: List<TransactionListViewItem>) {
        this.items = transactions
        notifyDataSetChanged()
    }

    class TransactionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val date: AppCompatTextView = itemView.findViewById(R.id.txt_txn_date)
        private val month: AppCompatTextView = itemView.findViewById(R.id.txt_txn_month)
        private val pot: AppCompatTextView = itemView.findViewById(R.id.txt_txn_pot)
        private val valueBase: NumberView = itemView.findViewById(R.id.txt_txn_value_base)
        private val valueAlternate: NumberView =
            itemView.findViewById(R.id.txt_pot_value_alternate)
        private val menu: AppCompatTextView = itemView.findViewById(R.id.transaction_item_menu)

        fun bind(
            item: TransactionListViewItem.TransactionListItemEntity,
            onDelete: () -> Unit
        ) {
            // TODO move logic to mapper and ui model
            valueBase.setNumber(item.amount, item.currency)
            if (item.amount != item.fxValue) {
                valueAlternate.setNumber(
                    item.fxValue,
                    item.defaultCurrency.first
                )
            } else {
                valueAlternate.text = ""
            }
            if (item.isFuture) {
                itemView.alpha = 0.65f
            } else {
                itemView.alpha = 1.0f
            }

            item.date!!.dayOfMonth
            date.text = item.date!!.dayOfMonth.toString()
            month.text = item.date!!.month.toString().substring(0,3)
            pot.text = item.potName

            menu.visibility = View.VISIBLE
            menu.setOnClickListener {
                val popup =
                    PopupMenu(itemView.context, menu)
                popup.inflate(R.menu.transaction_item_menu)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_delete_transaction -> {
                            item.id?.let { onDelete() }
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                popup.show()
            }
        }
    }

    class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val label: NumberView = itemView.findViewById(R.id.balance_label)
    }
}

