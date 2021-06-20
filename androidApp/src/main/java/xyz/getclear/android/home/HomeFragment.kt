package xyz.getclear.android.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import xyz.getclear.android.R
import xyz.getclear.android.common.ViewBindingHolder
import xyz.getclear.android.common.ViewBindingHolderImpl
import xyz.getclear.android.databinding.FragmentHomeBinding
import xyz.getclear.android.details.MiniTransactionListAdapter
import xyz.getclear.android.transaction.AddTransactionFragmentDirections
import xyz.getclear.domain.details.TransactionListViewItem
import xyz.getclear.domain.home.BalanceUiModel
import xyz.getclear.vm.home.HomeCommand
import xyz.getclear.vm.home.HomeViewModel
import xyz.getclear.vm.home.HomeViewState
import java.text.SimpleDateFormat
import java.util.*

val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

class HomeFragment : Fragment(),
    ViewBindingHolder<FragmentHomeBinding> by ViewBindingHolderImpl() {

    private val viewModel: HomeViewModel by inject()

    private var uiStateJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = initBinding(FragmentHomeBinding.inflate(layoutInflater), this) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireBinding {
            recentTransactions.btnAddTransactions.setOnClickListener { selectPot() }
            layoutNoTransactions.btnAddFirstTransaction.setOnClickListener { selectPot() }
            layoutNoPots.btnAddFirstPot.setOnClickListener {
                findNavController().navigate(AddTransactionFragmentDirections.actionAddPot(null))
            }
            cardSummary.slicesView.setOnCheckedChangeListener { group, checkedId ->
                group.findViewById<Chip>(checkedId)?.let {
                    viewModel.process(HomeCommand.SetStartDate(it.tag.toString()))
                    cardSummary.chart.isSelected = false
                }
            }
            cardSummary.slicesView.findViewWithTag<Chip>("3m")?.performClick()
            recentTransactions.btnShowAllTransactions.setOnClickListener {
                findNavController()
                    .navigate(HomeFragmentDirections.actionMenuTransactionsToFragAllTransactions())
            }
            cardSummary.chart.setOnChartValueSelectedListener(object :
                OnChartValueSelectedListener {
                override fun onNothingSelected() {}

                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    e?.let {
                        showSelectedEntry(it.y, dateFormat.format(it.x))
                    }
                }
            })
        }
        resetViews()
        uiStateJob = lifecycleScope.launch {

            viewModel.viewState.collect { state ->
                resetViews()
                requireBinding {
                    when (state) {
                        is HomeViewState.Loading -> {
                            progressHome.visibility = View.VISIBLE
                        }
                        is HomeViewState.Data -> {
                            cardSummary.root.visibility = View.VISIBLE
                            homeContainer.visibility = View.VISIBLE
                            displayTransactions(state.data)
                            showCurrencies(state.trackedCurrencies)
                            showBalance(state.balance)
                            state.chartEntries?.let { displayTransactionsChart(it) }
                        }
                        is HomeViewState.Error -> {
                            layoutError.viewHomeError.visibility = View.VISIBLE
                        }
                        is HomeViewState.NoPots -> {
                            layoutNoPots.viewNoPots.visibility = View.VISIBLE
                        }
                        is HomeViewState.NoTransactions -> {
                            layoutNoTransactions.root.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStop() {
        uiStateJob?.cancel()
        super.onStop()
    }

    private fun displayTransactionsChart(transactions: List<xyz.getclear.domain.reports.Entry>) {
        requireBinding().cardSummary.chart.run {
            invalidate()
            bind(transactions)
        }
    }

    private fun displayTransactions(transactions: List<TransactionListViewItem>) {
        MiniTransactionListAdapter(
            requireBinding().recentTransactions.miniListTransactions,
            transactions.filterIsInstance<TransactionListViewItem.TransactionListItemEntity>()
        ) { viewModel.process(HomeCommand.DeleteTransaction(it)) }
    }

    private fun resetViews() {
        requireBinding {
            cardSummary.root.visibility = View.GONE
            progressHome.visibility = View.GONE
            layoutNoPots.viewNoPots.visibility = View.GONE
            layoutNoTransactions.root.visibility = View.GONE
            homeContainer.visibility = View.GONE
            layoutError.viewHomeError.visibility = View.GONE
        }
    }

    private fun showBalance(balance: BalanceUiModel) {
        requireBinding {
            balanceLayout.txtBalance.setNumber(balance.value)
            if (balance.growth != null) {
                balanceLayout.txtHomeBalanceChange.text = balance.growth
                balanceLayout.txtHomeBalanceChangeDate.text = balance.dateLabel
            } else {
                balanceLayout.txtHomeBalanceChange.text = ""
                balanceLayout.txtHomeBalanceChangeDate.text = ""
            }
        }
    }

    private fun showSelectedEntry(value: Float, date: String) {
        requireBinding {
            cardSummary.txtHomeBalance.setNumber(value)
            cardSummary.txtHomeBalanceDate.text = date
        }
    }

    private fun showCurrencies(list: Map<String, Float>) {
        requireBinding {
            if (list.isEmpty()) {
                trackedCurrenciesView.root.visibility = View.GONE
            } else {
                trackedCurrenciesView.root.visibility = View.VISIBLE
                trackedCurrenciesView.currencyList.layoutManager =
                    LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
                trackedCurrenciesView.currencyList.adapter =
                    TrackedCurrenciesAdapter(list)
            }
        }
    }

    private fun selectPot() {
        findNavController().navigate(R.id.pick_pot)
    }
}