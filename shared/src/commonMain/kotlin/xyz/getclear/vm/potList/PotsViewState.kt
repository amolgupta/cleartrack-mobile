package xyz.getclear.vm.potList

sealed class PotsViewState {
    object Loading : PotsViewState()
    class Data(val data: List<PotUiModel>) : PotsViewState()
    object NoPots : PotsViewState()
    class Error(val error: String) : PotsViewState()
}

sealed class PotsCommand {
    object Start : PotsCommand()
    object AddPot : PotsCommand()
    data class DeletePot(val id: String) : PotsCommand()
}

sealed class PotsEvents {
    object AddPot : PotsEvents()
}