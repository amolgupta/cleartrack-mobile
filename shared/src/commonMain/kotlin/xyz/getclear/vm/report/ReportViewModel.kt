package xyz.getclear.vm.report

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.getclear.data.net.contract.DataRepository
import xyz.getclear.domain.reports.BarEntry
import xyz.getclear.domain.reports.Entry
import xyz.getclear.domain.reports.mappers.GrowthReportMapper
import xyz.getclear.domain.reports.mappers.RiskMapper
import xyz.getclear.domain.reports.mappers.TrackedCurrenciesReportMapper

class ReportViewModel : KoinComponent {
    private val dataRepository: DataRepository by inject()
    private val trackedCurrenciesReportMapper: TrackedCurrenciesReportMapper by inject()
    private val riskMapper: RiskMapper by inject()
    private val growthReportMapper: GrowthReportMapper by inject()
    private val scope: CoroutineScope by inject()

    private val _viewState = MutableStateFlow(ReportState())
    val viewState: StateFlow<ReportState> = _viewState

    init {
        scope.launch {
            val pots = dataRepository.getAllPots()
            _viewState.value =
                ReportState(
                    currencyReport = trackedCurrenciesReportMapper(pots),
                    riskReport = riskMapper(pots).toList(),
                    growthReport = growthReportMapper(pots)
                )
        }
    }
}

data class ReportState(
    val currencyReport: List<BarEntry> = emptyList(),
    val riskReport: List<Float?> = emptyList(),
    val growthReport: List<Entry> = emptyList()
)