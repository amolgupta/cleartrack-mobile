package xyz.getclear.vm.addPot

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.getclear.data.data.Pot
import xyz.getclear.data.net.CurrencyRepository
import xyz.getclear.data.net.contract.DataRepository
import xyz.getclear.domain.common.AnalyticsWrapper
import xyz.getclear.domain.common.EVENT_ADD_POT
import xyz.getclear.domain.pots.AppliedTags

class AddPotViewModel : KoinComponent {
    private val dataRepository: DataRepository by inject()
    private val currencyRepository: CurrencyRepository by inject()
    private val scope: CoroutineScope by inject()
    private val analyticsWrapper: AnalyticsWrapper by inject()
    private val appliedTags: AppliedTags by inject()
    private val additionalTags: MutableList<TagState> = mutableListOf()

    private var potId: String? = null

    private val _viewState = MutableStateFlow<AddPotViewState>(AddPotViewState.Loading)
    val viewState: StateFlow<AddPotViewState> = _viewState

    fun start(potId: String?) {
        this.potId = potId
        emitDataState()
    }

    private fun emitDataState(){
        scope.launch {
            val pot = potId?.let { dataRepository.getPot(it) }
            emitState(
                AddPotViewState.Data(
                    pot,
                    currencyRepository.getCurrencies().keys.toList(),
                    pot?.currency ?: currencyRepository.defaultCurrency().first,
                    appliedTags.of(pot, dataRepository.getAllPots()) + additionalTags
                )
            )
        }
    }

    fun addPot(name: String, currency: String, selectedTags: List<String>) {
        val pot = Pot(
            name = name,
            currency = currency,
            tags = selectedTags
        )
        handleUi {
            if (potId == null) {
                dataRepository.addPot(pot)
            } else {
                dataRepository.updatePot(pot.copy(id = potId))
            }
        }
    }

    fun addTag(tag: String) {
        additionalTags.add(TagState(tag, true))
        emitDataState()
    }

    private fun handleUi(block: suspend () -> Unit) {
        scope.launch {
            try {
                block()
                emitState(AddPotViewState.Success)
                analyticsWrapper.logEvent(EVENT_ADD_POT)

            } catch (e: Exception) {
                emitState(AddPotViewState.Error(e.message ?: "Error"))
            }
        }
    }

    private fun emitState(state: AddPotViewState) {
        _viewState.value = state
    }
}