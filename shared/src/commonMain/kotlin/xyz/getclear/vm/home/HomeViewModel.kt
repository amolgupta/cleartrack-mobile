package xyz.getclear.vm.home

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone.Companion.UTC
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.getclear.data.data.DateRange
import xyz.getclear.data.net.contract.DataRepository
import xyz.getclear.domain.common.AnalyticsWrapper
import xyz.getclear.domain.common.EVENT_DELETE_ADD_TRANSACTION
import xyz.getclear.domain.home.ChartMapper
import xyz.getclear.domain.home.GrowthMapper
import xyz.getclear.domain.home.PotsToTransactionListViewItemMapper
import xyz.getclear.domain.home.TrackedCurrenciesMapper

class HomeViewModel : ViewModel(), KoinComponent {
    private val dataRepository: DataRepository by inject()
    private val mapper: PotsToTransactionListViewItemMapper by inject()
    private val scope: CoroutineScope by inject()
    private val analyticsWrapper: AnalyticsWrapper by inject()
    private val trackedCurrenciesMapper: TrackedCurrenciesMapper by inject()
    private val growthMapper: GrowthMapper by inject()
    private val chartMapper: ChartMapper by inject()
    private var startDate = DateRange.THREE_MONTH

    private val _viewState = MutableLiveData<HomeViewState>(HomeViewState.Loading)
    val viewState: LiveData<HomeViewState> = _viewState

    fun process(command: HomeCommand) {
        when (command) {
            HomeCommand.Start -> update()
            is HomeCommand.SetStartDate -> setStartDate(DateRange.findByTag(command.tag))
            is HomeCommand.DeleteTransaction -> deleteTransaction(command.id)
        }
    }

    private fun update() {
        emitState(HomeViewState.Loading)
        scope.launch {
            try {
                val items = dataRepository.getAllPots()
                if (items.isNullOrEmpty()) emitState(HomeViewState.NoPots)
                else if (items.flatMap { it.transactions }.isEmpty())
                    emitState(HomeViewState.NoTransactions)
                else emitState(
                    HomeViewState.Data(
                        data = mapper(items),
                        trackedCurrencies = trackedCurrenciesMapper(from = dataRepository.getAllPots()),
                        balance = growthMapper.invoke(items, startDate.getStartTime()),
                        chartEntries = chartMapper.getData(items, startDate)
                    )
                )
            } catch (e: Exception) {
                emitState(HomeViewState.Error(e.message ?: "Error"))
            }
        }
    }

    private fun deleteTransaction(id: String) {
        scope.launch {
            id.let {
                try {
                    dataRepository.deleteTransaction(it)
                    update()
                    analyticsWrapper.logEvent(EVENT_DELETE_ADD_TRANSACTION)
                } catch (e: Exception) {
                    emitState(HomeViewState.Error("Error deleting transaction"))
                }
            }
        }
    }

    private fun setStartDate(dateRange: DateRange) {
        startDate = dateRange
        update()
    }

    private fun emitState(state: HomeViewState) {
        _viewState.postValue(state)
    }

    private fun DateRange.getStartTime(): LocalDate {
        return Clock.System.todayAt(UTC)
            .minus(this.days, DateTimeUnit.DAY)
    }
}