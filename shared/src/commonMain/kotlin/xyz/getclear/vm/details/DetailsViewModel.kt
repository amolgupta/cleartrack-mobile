package xyz.getclear.vm.details

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.getclear.data.data.DateRange
import xyz.getclear.data.net.contract.DataRepository
import xyz.getclear.domain.details.TransactionViewItemMapper
import xyz.getclear.domain.home.ChartMapper

class DetailsViewModel : ViewModel(), KoinComponent {
    private val dataRepository: DataRepository by inject()
    private val scope: CoroutineScope by inject()
    private val chartMapper: ChartMapper by inject()
    private val transactionViewItemMapper: TransactionViewItemMapper by inject()

    private lateinit var potId: String
    private var dateRange = DateRange.THREE_MONTH
    private val _viewState = MutableStateFlow<DetailsViewState?>(null)
    private val _events = MutableStateFlow<DetailsEvents?>(null)

    val viewState: StateFlow<DetailsViewState?> = _viewState
    val events: StateFlow<DetailsEvents?> = _events

    fun start(potId: String) {
        this.potId = potId
        updateState()
    }

    fun updateDate(dateRange: DateRange) {
        this.dateRange = dateRange
        updateState()
    }

    private fun updateState() {
        scope.launch {
            dataRepository.getPot(potId)?.let { pot ->
                _viewState.value = dataRepository.getPot(potId)?.let {
                    DetailsViewState(
                        title = pot.name,
                        data = transactionViewItemMapper(pot),
                        entries = chartMapper.getData(
                            it,
                            dateRange
                        ),
                        tags = pot.tags
                    )
                }
            }
        }
    }

    fun deleteTransaction(id: String) {
        scope.launch {
            try {
                dataRepository.deleteTransaction(id)
                updateState()
            } catch (e: Exception) {
                _events.value = e.message?.let { DetailsEvents.Error(it) }
            }
        }
    }

    fun deletePot() {
        scope.launch {
            potId.let {
                try {
                    dataRepository.deletePot(it)
                } catch (e: Exception) {
                    _events.value = DetailsEvents.Error(e.message ?: "Error")
                }
            }
        }
    }
}