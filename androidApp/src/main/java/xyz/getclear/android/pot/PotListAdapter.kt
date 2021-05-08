package xyz.getclear.android.pot

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import xyz.getclear.android.R
import xyz.getclear.android.common.toChartEntry
import xyz.getclear.android.databinding.ItemPotBinding
import xyz.getclear.vm.potList.PotUiModel

class PotAdapter: RecyclerView.Adapter<PotAdapter.ViewHolder>() {
    private var pots: List<PotUiModel> = listOf()
    var listener: PotItemListener? = null
    var showMenu: Boolean = true

    var onSelect: ((potId: String) -> Unit)? = null

    override fun getItemCount(): Int {
        return pots.size
    }

    fun submitList(pots: List<PotUiModel>) {
        this.pots = pots
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemPotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            Log.d("PotAdapter", " is drawing")
        }
        val item = pots[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            item.id.let { it1 -> onSelect?.let { it2 -> it2(it1) } }
        }

        if (showMenu) {
            holder.itemViewBinding.potItemMenu.visibility = View.VISIBLE
            holder.itemViewBinding.potItemMenu.setOnClickListener {
                val popup = PopupMenu(holder.itemView.context, holder.itemViewBinding.potItemMenu)
                popup.inflate(R.menu.pot_menu)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_delete_pot -> {
                            listener?.onDeleteClicked(item.id, position)
                        }
                        R.id.menu_add_transaction -> {
                            item.id.let { it1 -> listener?.onAddTransactionSelected(it1) }
                        }
                        R.id.menu_edit -> {
                            item.id.let { it1 -> listener?.onEditClicked(it1) }
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                popup.show()
            }
        }
    }

    class ViewHolder(val itemViewBinding: ItemPotBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {

        fun bind(pot: PotUiModel) {
            itemViewBinding.txtPotName.text = pot.name
            itemViewBinding.txtPotValue.setNumber(pot.balance)
            itemViewBinding.chartPot.draw(pot.entries.map { it.toChartEntry() })
            itemViewBinding.txtPotCurrency.text = pot.currency
        }
    }

    interface PotItemListener {
        fun onSelected(potId: String)
        fun onEditClicked(potId: String)
        fun onDeleteClicked(potId: String, position: Int)
        fun onAddTransactionSelected(potId: String)
    }
}
