package xyz.getclear.android.pot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import xyz.getclear.android.R
import xyz.getclear.android.databinding.FragmentPotsBinding
import xyz.getclear.android.details.DetailsFragmentDirections
import xyz.getclear.android.transaction.AddTransactionFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel
import xyz.getclear.android.common.ViewBindingHolder
import xyz.getclear.android.common.ViewBindingHolderImpl
import xyz.getclear.vm.potList.*

class PotsFragment : Fragment(),
    ViewBindingHolder<FragmentPotsBinding> by ViewBindingHolderImpl() {

    private val model: PotsViewModel by viewModel()

    private val adapter = PotAdapter()

    private var uiStateJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = initBinding(FragmentPotsBinding.inflate(layoutInflater), this) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireBinding {
            listPots.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
            fab.setOnClickListener { model.process(PotsCommand.AddPot) }
            listPots.adapter = adapter
        }
        adapter.apply {
            onSelect = {
                findNavController().navigate(DetailsFragmentDirections.showPotDetails(it))
            }

            listener = object : PotAdapter.PotItemListener {
                override fun onSelected(potId: String) {}

                override fun onEditClicked(potId: String) {
                    findNavController().navigate(AddPotFragmentDirections.actionAddPot(potId))
                }

                override fun onDeleteClicked(potId: String, position: Int) {
                    showDeleteConfirmation(potId)
                }

                override fun onAddTransactionSelected(potId: String) {
                    findNavController().navigate(
                        AddTransactionFragmentDirections.addTransaction(potId)
                    )
                }
            }
        }
        model.process(PotsCommand.Start)
        uiStateJob = lifecycleScope.launch {

            model.viewState.collect { state ->
                when (state) {
                    is PotsViewState.Loading -> {
                        setState(State.PROGRESS)
                    }
                    is PotsViewState.Data -> {
                        setState(State.DATA)
                        displayPots(ArrayList(state.data))
                    }
                    PotsViewState.NoPots -> {
                        setState(State.NO_DATA)
                        displayPots(arrayListOf())
                    }
                    is PotsViewState.Error -> setState(State.ERROR)
                }
            }
        }
        model.eventsFlow.onEach {
            when (it) {
                PotsEvents.AddPot -> addPot()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun displayPots(pots: ArrayList<PotUiModel>) {
        adapter.submitList(pots)
    }


    private fun setState(state: State) {
        requireBinding().run {
            when (state) {
                State.PROGRESS -> {
                    viewNoPots.root.visibility = View.GONE
                    progressPots.visibility = View.VISIBLE
                }
                State.DATA -> {
                    viewNoPots.root.visibility = View.GONE
                    progressPots.visibility = View.GONE
                }
                State.ERROR -> {
                    viewNoPots.root.visibility = View.GONE
                    progressPots.visibility = View.GONE
                }
                State.NO_DATA -> {
                    viewNoPots.root.visibility = View.VISIBLE
                    progressPots.visibility = View.GONE
                }
            }
        }
    }

    private fun showDeleteConfirmation(id: String) {
        AlertDialog.Builder(
            ContextThemeWrapper(
                requireContext(),
                R.style.AppTheme_Dialog
            )
        )
            .setTitle(getString(R.string.delete_account_dialog_title))
            .setCancelable(true)
            .setMessage(getString(R.string.delete_account_dialog_message))
            .setNegativeButton(getString(R.string.cancel_cta)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes_cta)) { dialog, _ ->
                model.process(PotsCommand.DeletePot(id))
                dialog.dismiss()
            }
            .show()
    }
    override fun onStop() {
        uiStateJob?.cancel()
        super.onStop()
    }

    private fun addPot() {
        findNavController().navigate(AddTransactionFragmentDirections.actionAddPot(null))
    }
}