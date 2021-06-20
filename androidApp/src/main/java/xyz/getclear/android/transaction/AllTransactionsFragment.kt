package xyz.getclear.android.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import xyz.getclear.android.databinding.FragmentAllTransactionsBinding
import org.koin.android.ext.android.inject
import xyz.getclear.android.common.ViewBindingHolder
import xyz.getclear.android.common.ViewBindingHolderImpl
import xyz.getclear.android.details.TransactionsAdapter
import xyz.getclear.vm.home.HomeCommand
import xyz.getclear.vm.home.HomeViewModel
import xyz.getclear.vm.home.HomeViewState
import xyz.getclear.domain.details.TransactionListViewItem

class AllTransactionsFragment : Fragment(),
    ViewBindingHolder<FragmentAllTransactionsBinding> by ViewBindingHolderImpl() {

    private val model: HomeViewModel by inject()

    private var uiStateJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = initBinding(FragmentAllTransactionsBinding.inflate(layoutInflater), this) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireBinding().listTransactions.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        requireBinding().listTransactions.adapter = adapter

        model.process(HomeCommand.Start)
        uiStateJob = lifecycleScope.launch {

            model.viewState.collect { state ->
                when (state) {
                    is HomeViewState.Data -> {
                        displayTransactions(state.data)
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private val adapter: TransactionsAdapter by lazy {
        TransactionsAdapter(listOf()) {
            model.process(HomeCommand.DeleteTransaction(it))
        }
    }

    private fun displayTransactions(data: List<TransactionListViewItem>) {
        adapter.submitList(data)
    }
}