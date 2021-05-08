package xyz.getclear.android.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xyz.getclear.android.R
import xyz.getclear.android.design.NumberView

class TrackedCurrenciesAdapter constructor(val list: Map<String, Float>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val keys = list.keys.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_currency, parent, false)
        return CurrencyViewHolder(view)
    }

    override fun getItemCount(): Int =
        list.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as CurrencyViewHolder
        holder.label.text = keys[position]
        list[keys[position]]?.let { holder.value.setNumber(it) }
    }

    class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val label: TextView = itemView.findViewById(R.id.currency_name)
        val value: NumberView = itemView.findViewById(R.id.currency_value)
    }
}
