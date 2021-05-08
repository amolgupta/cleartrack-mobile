package xyz.getclear.vm.pots

sealed class PotsPickerViewState {
    object Loading : PotsPickerViewState()
    class Data(val data: List<PotPickerUiModel>) : PotsPickerViewState()
    object NoPots : PotsPickerViewState()
    class Error(val error: String) : PotsPickerViewState()
}

sealed class PotPickerCommand {
    object Start : PotPickerCommand()
}