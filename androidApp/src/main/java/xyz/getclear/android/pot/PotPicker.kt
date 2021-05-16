package xyz.getclear.android.pot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import xyz.getclear.android.databinding.FragmentPotsPickerBinding
import xyz.getclear.android.transaction.AddTransactionFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel
import xyz.getclear.android.common.ViewBindingHolder
import xyz.getclear.android.common.ViewBindingHolderImpl
import xyz.getclear.vm.potPicker.PotPickerViewModel
import xyz.getclear.vm.pots.PotPickerCommand
import xyz.getclear.vm.pots.PotPickerUiModel
import xyz.getclear.vm.pots.PotsPickerViewState

class PotPicker : Fragment(),
    ViewBindingHolder<FragmentPotsPickerBinding> by ViewBindingHolderImpl() {

    private val potPickerAdapter by lazy {
        PotPickerAdapter().apply {
            onSelect = {
                findNavController().navigate(AddTransactionFragmentDirections.addTransaction(it))
            }
        }
    }
    private var uiStateJob: Job? = null

    private val model: PotPickerViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = initBinding(FragmentPotsPickerBinding.inflate(layoutInflater), this) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireBinding {
            listPots.apply {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                listPots.adapter = potPickerAdapter
            }
        }
        model.process(PotPickerCommand.Start)
        uiStateJob = lifecycleScope.launch {

            model.pickerViewState.collect { state ->
                when (state) {
                    is PotsPickerViewState.Loading -> {
                    }
                    is PotsPickerViewState.Data -> {
                        displayPots(state.data)
                    }
                    else -> throw IllegalStateException()
                }
            }
        }
    }
    override fun onStop() {
        uiStateJob?.cancel()
        super.onStop()
    }

    private fun displayPots(pots: List<PotPickerUiModel>) {
        potPickerAdapter.submitList(pots)
    }
}