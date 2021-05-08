package xyz.getclear.android.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import kotlinx.datetime.LocalDate
import org.koin.androidx.viewmodel.ext.android.viewModel
import xyz.getclear.android.common.ViewBindingHolder
import xyz.getclear.android.common.ViewBindingHolderImpl
import xyz.getclear.android.databinding.FragmentAddTransactionBinding
import xyz.getclear.vm.addTransaction.AddTransactionViewModel
import xyz.getclear.vm.addTransaction.AddTransactionViewState

// Refactor and move logic to VM
class AddTransactionFragment : Fragment(),
    ViewBindingHolder<FragmentAddTransactionBinding> by ViewBindingHolderImpl() {

    lateinit var date: LocalDate
    private var editingTransaction = false
    private var editingBalance = false

    private val model: AddTransactionViewModel by viewModel()

    private val args: AddTransactionFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.start(args.potId)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        initBinding(FragmentAddTransactionBinding.inflate(layoutInflater), this) {
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.viewState.addObserver { state ->
            when (state) {
                is AddTransactionViewState.Error -> {
                    showError(message = state.message)
                }
                is AddTransactionViewState.Data -> {
                    requireBinding {
                        progressbar.isVisible = false
                        txnAddToolbar.title = state.title
                    }
                    date = state.date
                    setupTextFields(state.balance)
                }
                is AddTransactionViewState.Loading -> {
                    requireBinding().progressbar.isVisible = true
                }
                is AddTransactionViewState.Complete -> {
                    requireBinding().progressbar.isVisible = false
                    findNavController().navigateUp()
                }
                else -> {
                }
            }
        }

        requireBinding {

            edtTxnValue.requestFocus()

            calTxnDate.setOnDateChangeListener { _, year, month, day ->
                date = LocalDate(year, month + 1, day)
            }

            btnAddTxn.setOnClickListener {
                if (!isValidAmount(edtTxnValue.text.toString())) {
                    edtTxnValue.error = "Enter a valid amount"
                } else {
                    model.addTransaction(
                        edtTxnValue.text.toString().toFloat(),
                        date
                    )
                }
            }
        }
    }

    private fun setupTextFields(balance: Float) {
        requireBinding {
            setText(edtPotBalance, balance)

            edtTxnValue.doBeforeTextChanged { _, _, _, _ ->
                editingTransaction = true
            }
            edtPotBalance.doBeforeTextChanged { _, _, _, _ ->
                editingBalance = true
            }
            edtTxnValue.doAfterTextChanged { text ->
                if (!editingBalance && isValidAmount(text.toString()))
                    setText(edtPotBalance, balance + text.toString().toFloat())
                editingTransaction = false
            }

            edtPotBalance.doAfterTextChanged { text ->
                if (!editingTransaction && isValidAmount(text.toString()))
                    setText(edtTxnValue, text.toString().toFloat().minus(balance))
                editingBalance = false
            }

            edtTxnValue.setOnEditorActionListener { _, _, _ ->
                false

            }
            edtPotBalance.setOnEditorActionListener { _, _, _ ->
                false
            }
        }
    }

    private fun isValidAmount(input: String): Boolean {
        return try {
            input.isNotEmpty() && !input.toDouble().isNaN()
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun setText(view: EditText, double: Float) {
        view.setText(String.format("%.2f", double))
    }

    private fun showError(message: String) {
        Snackbar.make(
            requireBinding().root,
            message, Snackbar.LENGTH_SHORT
        ).show()
    }
}
