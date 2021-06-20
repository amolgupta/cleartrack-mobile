package xyz.getclear.android.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import xyz.getclear.android.R
import xyz.getclear.android.common.ViewBindingHolder
import xyz.getclear.android.common.ViewBindingHolderImpl
import xyz.getclear.android.databinding.FragmentDetailsBinding
import xyz.getclear.data.data.DateRange
import xyz.getclear.vm.details.DetailsEvents
import xyz.getclear.vm.details.DetailsViewModel
import kotlinx.coroutines.flow.collect

class DetailsFragment : Fragment(), Toolbar.OnMenuItemClickListener,
    ViewBindingHolder<FragmentDetailsBinding> by ViewBindingHolderImpl() {

    private lateinit var bottomAppBar: BottomAppBar

    private val args: DetailsFragmentArgs by navArgs()

    private val viewModel: DetailsViewModel by inject()

    private var uiStateJob: Job? = null

    private val adapter: TransactionsAdapter by lazy {
        TransactionsAdapter(emptyList()) {
            viewModel.deleteTransaction(it)
        }
    }

    private lateinit var potId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args.let { potId = args.potId }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = initBinding(FragmentDetailsBinding.inflate(layoutInflater), this) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomAppBar = view.findViewById(R.id.bar_details) as BottomAppBar
        bottomAppBar.setOnMenuItemClickListener(this)

        requireBinding {
            listTransactions.layoutManager =
                LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            listTransactions.layoutManager =
                LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            cardSummary.slicesView.apply {
                findViewWithTag<View>("3m").performClick()
                setOnCheckedChangeListener { group, checkedId ->
                    group.findViewById<View>(checkedId)?.let {
                        viewModel.updateDate(DateRange.findByTag(it.tag.toString()))
                    }
                }
            }
        }
        viewModel.start(potId)
        uiStateJob = lifecycleScope.launch {

            viewModel.viewState.collect { state ->
                state?.let { renderState(it, adapter) }
            }
            viewModel.events.collect { event ->
                when (event) {
                    is DetailsEvents.Error -> showError(bottomAppBar, event.error)
                    DetailsEvents.Deleted -> activity?.finish()
                }
            }
        }
    }

    override fun onStop() {
        uiStateJob?.cancel()
        super.onStop()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_add_transaction -> addTransaction(potId)
            R.id.menu_delete_pot -> confirmDeletePot { viewModel.deletePot() }
            R.id.menu_hook -> showWebHookDialog(potId)
            R.id.menu_edit -> editAccount(potId)
        }
        return true
    }
}

