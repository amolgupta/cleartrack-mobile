package xyz.getclear.android.pot

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.getclear.android.databinding.ItemPotBinding
import xyz.getclear.vm.pots.PotPickerUiModel

class PotPickerAdapter: RecyclerView.Adapter<PotPickerAdapter.ViewHolder>() {
    private var pots: List<PotPickerUiModel> = listOf()
    var onSelect: ((potId: String) -> Unit)? = null

    override fun getItemCount() = pots.size

    fun submitList(pots: List<PotPickerUiModel>) {
        this.pots = pots
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemPotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("PotAdapter", " is drawing")
        val item = pots[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            item.id.let { it1 -> onSelect?.let { it2 -> it2(it1) } }
        }
    }

    class ViewHolder(private val itemViewBinding: ItemPotBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {

        fun bind(pot: PotPickerUiModel) {
            itemViewBinding.txtPotName.text = pot.name
            itemViewBinding.txtPotValue.setNumber(pot.balance)
            itemViewBinding.txtPotCurrency.text = pot.currency
        }
    }
}
