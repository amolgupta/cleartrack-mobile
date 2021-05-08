package xyz.getclear.android.details

import android.annotation.SuppressLint
import android.util.Base64
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import xyz.getclear.android.R
import xyz.getclear.domain.details.TransactionListViewItem
import xyz.getclear.vm.details.DetailsViewState
import xyz.getclear.android.details.DetailsFragmentDirections
import xyz.getclear.android.pot.AddPotFragmentDirections

fun DetailsFragment.showWebHookDialog(potId: String) {
    findNavController().navigate(
        DetailsFragmentDirections.actionFragDetailsToDialogWebhook(
            Base64.encodeToString(potId.encodeToByteArray(), Base64.DEFAULT)
        )
    )
}

fun DetailsFragment.editAccount(potId: String) {
    findNavController().navigate(AddPotFragmentDirections.actionAddPot(potId))
}

fun DetailsFragment.confirmDeletePot(block: () -> Unit) {
    AlertDialog.Builder(
        ContextThemeWrapper(requireContext(), R.style.AppTheme_Dialog)
    ).setTitle("Are you sure you want to delete this account?")
        .setPositiveButton("Yes") { view, _ ->
            block()
            view.dismiss()
        }.setNegativeButton("Cancel") { view, _ ->
            view.dismiss()
        }
        .setMessage(getString(R.string.pot_delete_message))
        .show()
}

fun DetailsFragment.addTransaction(potId: String) {
    findNavController().navigate(
        DetailsFragmentDirections.actionFragDetailsToMenuAddTransaction(potId)
    )
}

fun DetailsFragment.displayTransactionList(
    adapter: TransactionsAdapter,
    transactions: List<TransactionListViewItem>
) {
    adapter.submitList(transactions)
    requireBinding().listTransactions.adapter = adapter
}

fun DetailsFragment.displayTransactionChart(transactions: List<xyz.getclear.domain.reports.Entry>) {
    requireBinding().cardSummary.chart.run {
        invalidate()
        bind(transactions)
    }
}

@SuppressLint("ShowToast")
fun DetailsFragment.showError(
    bottomAppBar: BottomAppBar,
    message: String
) {
    Snackbar.make(
        requireBinding().root,
        message, Snackbar.LENGTH_SHORT
    ).setAnchorView(bottomAppBar).show()
}

fun DetailsFragment.showTags(tags: List<String>) {
    requireBinding().tags.removeAllViews()
    tags.forEach { tag ->
        Chip(requireContext()).apply {
            text = tag
            isClickable = false
        }.let {
            requireBinding { this.tags.addView(it) }
        }
    }
}

fun DetailsFragment.renderState(state: DetailsViewState, adapter: TransactionsAdapter) {
    displayTransactionList(adapter, state.data)
    requireBinding().displayTitle(state.title)
    state.entries?.let { displayTransactionChart(it) }
    showTags(state.tags)
}