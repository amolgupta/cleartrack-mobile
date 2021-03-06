package xyz.getclear.android.pot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import kotlinx.coroutines.launch
import xyz.getclear.android.common.ViewBindingHolder
import xyz.getclear.android.common.ViewBindingHolderImpl
import xyz.getclear.android.databinding.FragmentAddPotBinding
import xyz.getclear.vm.addPot.AddPotViewModel
import xyz.getclear.vm.addPot.AddPotViewState

class AddPotFragment : Fragment(),
    ViewBindingHolder<FragmentAddPotBinding> by ViewBindingHolderImpl() {
    private val args: AddPotFragmentArgs by navArgs()
    private var potId: String? = null
    private var uiStateJob: Job? = null

    private val model: AddPotViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        potId = args.potId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = initBinding(FragmentAddPotBinding.inflate(layoutInflater), this) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireBinding {
            edtPotName.requestFocus()

            btnAddNewPot.setOnClickListener {
                if (edtPotName.editText?.text!!.isBlank()) {
                    edtPotName.error = "Enter a name"
                } else {
                    edtPotName.error = null
                    model.addPot(
                        edtPotName.editText?.text.toString(),
                        spnPotCurrency.selectedItem.toString(),
                        getSelectedTags()
                    )
                }
            }
        }
        model.start(potId)

        uiStateJob = lifecycleScope.launch {

            model.viewState.collect { state ->
                when (state) {
                    is AddPotViewState.Data -> renderState(state) {
                        model.addTag(it)
                    }
                    is AddPotViewState.Error -> showError(state.error)
                    is AddPotViewState.Success -> finish()
                    is AddPotViewState.Loading -> {
                    }
                }
            }
        }
    }

    override fun onStop() {
        uiStateJob?.cancel()
        super.onStop()
    }
}