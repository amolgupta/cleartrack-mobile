package xyz.getclear.vm.potPicker

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.getclear.data.net.contract.DataRepository
import xyz.getclear.vm.pots.PotPickerCommand
import xyz.getclear.vm.pots.PotToPotPickerUiModelMapper
import xyz.getclear.vm.pots.PotsPickerViewState

class PotPickerViewModel : ViewModel(), KoinComponent {
    private val dataRepository: DataRepository by inject()
    private val scope: CoroutineScope by inject()
    private val mapper: PotToPotPickerUiModelMapper by inject()

    private val _viewState = MutableStateFlow<PotsPickerViewState>(PotsPickerViewState.Loading)
    val pickerViewState: StateFlow<PotsPickerViewState> = _viewState

    fun process(command: PotPickerCommand) {
        when (command) {
            PotPickerCommand.Start -> start()
        }
    }

    private fun start() {
        emmitState(PotsPickerViewState.Loading)
        scope.launch {
            try {
                val pots = dataRepository.getAllPots()
                if (pots.isNotEmpty()) {
                    emmitState(
                        PotsPickerViewState.Data(
                            pots.sortedBy { pot -> pot.name }.map { mapper(it) }
                        ))
                } else {
                    emmitState(PotsPickerViewState.NoPots)
                }
            } catch (e: Exception) {
                emmitState(PotsPickerViewState.Error(e.message ?: "Error"))
            }
        }
    }

    private fun emmitState(state: PotsPickerViewState) {
        _viewState.value = state
    }
}