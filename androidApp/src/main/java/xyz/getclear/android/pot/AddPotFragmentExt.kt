package xyz.getclear.android.pot

import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.forEach
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.snackbar.Snackbar
import xyz.getclear.android.R
import xyz.getclear.vm.addPot.AddPotViewState
import android.content.DialogInterface
import android.text.InputType

import android.widget.EditText


private fun AddPotFragment.showCurrencies(currencies: List<String>, default: String) {
    requireBinding {
        spnPotCurrency.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_item, currencies)
        spnPotCurrency.setSelection(currencies.indexOf(default))
    }
}

fun AddPotFragment.showError(message: String?) =
    message?.let {
        Snackbar.make(
            requireView().findViewById(R.id.add_pot_container),
            it, Snackbar.LENGTH_SHORT
        ).show()
    }

fun AddPotFragment.finish() = NavHostFragment.findNavController(this).navigateUp()

fun AddPotFragment.renderState(state: AddPotViewState.Data, block: (tag: String) -> Unit) {

    showCurrencies(state.currencies, state.defaultCurrency)
    requireBinding {
        state.pot?.let {
            edtPotName.editText?.setText(it.name)
            spnPotCurrency.isEnabled = false
        }
        tags.removeAllViews()
        state.tags.forEach { tag ->
            Chip(requireContext()).apply {
                this.tag = tag.name
                text = tag.name
                isChecked = tag.isApplied
                closeIcon = ResourcesCompat.getDrawable(
                    resources,
                    android.R.drawable.ic_menu_close_clear_cancel,
                    null
                )
            }.let {
                tags.addView(it)
            }
        }
        tags.addView(getAddTagView(block))
    }
}

fun AddPotFragment.getAddTagView(block: (tag: String) -> Unit): Chip {
    return Chip(requireContext()).apply {
        text = "+"
        isChecked = false
        setOnClickListener {
            showAddTagDialog(block)
        }
    }
}

fun AddPotFragment.showAddTagDialog(block: (tag: String) -> Unit) {
    val builder = AlertDialog.Builder(requireContext())
    val input = EditText(requireContext())
    builder.setView(input)
    builder.setPositiveButton("OK") { _, _ -> block(input.text.toString()) }
    builder.setNegativeButton(
        "Cancel"
    ) { dialog, _ -> dialog.cancel() }
    builder.show()
}

fun AddPotFragment.getSelectedTags(): List<String> {
    val tags = mutableListOf<String>()
    requireBinding().tags.forEach {
        it as Chip
        if (it.isChecked) tags.add(it.tag as String)
    }
    return tags
}