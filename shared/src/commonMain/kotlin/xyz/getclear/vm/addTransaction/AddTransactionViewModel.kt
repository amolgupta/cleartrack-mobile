package xyz.getclear.vm.addTransaction

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.getclear.data.data.Transaction
import xyz.getclear.data.net.CurrencyRepository
import xyz.getclear.data.net.contract.DataRepository
import xyz.getclear.data.utils.currentBalance
import xyz.getclear.domain.common.AnalyticsWrapper
import xyz.getclear.domain.common.EVENT_ADD_TRANSACTION

class AddTransactionViewModel : ViewModel(), KoinComponent {
    private val dataRepository: DataRepository by inject()
    private val currencyRepository: CurrencyRepository by inject()
    private val scope: CoroutineScope by inject()
    private val analyticsWrapper: AnalyticsWrapper by inject()
    private val today: LocalDate by inject()
    private var potId: String? = null

    val viewState = MutableStateFlow<AddTransactionViewState>(AddTransactionViewState.Loading)

    fun addTransaction(value: Float, date: LocalDate) {
        val transaction = potId?.let {
            Transaction(
                id = null,
                amount = value,
                usdAmount = currencyRepository.getUSDValue(value),
                date = date,
                pot = it
            )
        }
        handleUi {
            transaction?.let { dataRepository.addTransaction(it) }
            transaction
        }
        analyticsWrapper.logEvent(EVENT_ADD_TRANSACTION)

    }

    private fun handleUi(block: suspend () -> Transaction?) {
        updateValue(AddTransactionViewState.Loading)
        scope.launch {
            try {
                val tx = block()
                if (tx != null && isActive) {
                    updateValue(AddTransactionViewState.Complete)
                } else {
                    updateValue(AddTransactionViewState.Error("Unable to update"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                updateValue(e.message.let { AddTransactionViewState.Error(it ?: "Error") })
            }
        }

    }

    fun start(potId: String?) {
        this.potId = potId
        scope.launch {
            val pot = potId?.let { dataRepository.getPot(it) }
            if (pot == null) {
                updateValue(AddTransactionViewState.Error("Pot not found"))
            } else {
                updateValue(
                    AddTransactionViewState.Data(
                        pot.name,
                        pot.currentBalance(),
                        today
                    )
                )
            }
        }
    }

    private fun updateValue(state: AddTransactionViewState) {
        viewState.value = state
    }
}