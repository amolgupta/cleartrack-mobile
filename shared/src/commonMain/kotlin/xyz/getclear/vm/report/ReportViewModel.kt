package xyz.getclear.vm.report

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.getclear.data.net.contract.DataRepository
import xyz.getclear.domain.reports.BarEntry
import xyz.getclear.domain.reports.Entry
import xyz.getclear.domain.reports.mappers.GrowthReportMapper
import xyz.getclear.domain.reports.mappers.RiskMapper
import xyz.getclear.domain.reports.mappers.TrackedCurrenciesReportMapper

class ReportViewModel : ViewModel(), KoinComponent {
    private val dataRepository: DataRepository by inject()
    private val trackedCurrenciesReportMapper: TrackedCurrenciesReportMapper by inject()
    private val riskMapper: RiskMapper by inject()
    private val growthReportMapper: GrowthReportMapper by inject()
    private val scope: CoroutineScope by inject()

    private val _viewState = MutableLiveData(ReportState())
    val viewState: LiveData<ReportState> = _viewState

    init {
        scope.launch {
            val pots = dataRepository.getAllPots()
            _viewState.postValue(
                ReportState
                    (
                    currencyReport = trackedCurrenciesReportMapper(pots),
                    riskReport = riskMapper(pots).toList(),
                    growthReport = growthReportMapper(pots)
                )
            )
        }
    }
}

data class ReportState(
    val currencyReport: List<BarEntry> = emptyList(),
    val riskReport: List<Float?> = emptyList(),
    val growthReport: List<Entry> = emptyList()
)