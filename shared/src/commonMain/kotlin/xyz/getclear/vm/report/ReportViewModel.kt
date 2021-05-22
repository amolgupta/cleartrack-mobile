package xyz.getclear.vm.report

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.getclear.data.net.contract.DataRepository

class ReportViewModel : ViewModel(), KoinComponent {

    private val dataRepository: DataRepository by inject()
    private val scope: CoroutineScope by inject()
    private val growthAdapter: GrowthReportAdapter by inject()
    private val currencyReportAdapter: CurrencyReportAdapter by inject()

    private val reports = listOf(growthAdapter,currencyReportAdapter)

    private val _viewState = MutableStateFlow(ReportState())
    val viewState: StateFlow<ReportState> = _viewState

    init {
        scope.launch {
            val pots = dataRepository.getAllPots()
            _viewState.value =
                ReportState(reports = reports.map {it.getReport(pots)})
        }
    }
}

