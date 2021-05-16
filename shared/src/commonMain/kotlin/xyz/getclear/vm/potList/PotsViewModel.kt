package xyz.getclear.vm.potList

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.getclear.data.net.contract.DataRepository
import xyz.getclear.domain.common.AnalyticsWrapper
import xyz.getclear.domain.common.EVENT_ADD_POT_INITIALIZED
import xyz.getclear.domain.common.EVENT_DELETE_POT
import xyz.getclear.domain.pots.PotToPotUiModelMapper

class PotsViewModel : ViewModel(), KoinComponent {
    private val dataRepository: DataRepository by inject()
    private val scope: CoroutineScope by inject()
    private val analyticsWrapper: AnalyticsWrapper by inject()
    private val mapper: PotToPotUiModelMapper by inject()

    private val _viewState = MutableStateFlow<PotsViewState>(PotsViewState.Loading)
    val viewState: StateFlow<PotsViewState> = _viewState

    private val eventChannel = Channel<PotsEvents>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun process(command: PotsCommand) {
        when (command) {
            PotsCommand.Start -> start()
            PotsCommand.AddPot -> addPot()
            is PotsCommand.DeletePot -> deletePot(command.id)
        }
    }

    private fun start() {
        emmitState(PotsViewState.Loading)
        scope.launch {
            try {
                val pots = dataRepository.getAllPots()
                if (pots.isNotEmpty()) {
                    emmitState(
                        PotsViewState.Data(
                            pots.sortedBy { pot -> pot.name }.map { mapper(it) }
                        ))
                } else {
                    emmitState(PotsViewState.NoPots)
                }
            } catch (e: Exception) {
                emmitState(PotsViewState.Error(e.message ?: "Error"))
            }
        }
    }


    private fun deletePot(id: String) {
        scope.launch {
            try {
                dataRepository.deletePot(id)
                start()
                analyticsWrapper.logEvent(EVENT_DELETE_POT)

            } catch (e: Exception) {
                emmitState(PotsViewState.Error(e.message ?: "Error"))
            }
        }
    }

    private fun emmitState(state: PotsViewState) {
        _viewState.value = state
    }

    private fun addPot() {
        scope.launch {
            eventChannel.send(PotsEvents.AddPot)
        }
        analyticsWrapper.logEvent(EVENT_ADD_POT_INITIALIZED)
    }
}