package xyz.getclear.vm.addPot

import xyz.getclear.data.data.Pot

sealed class AddPotViewState {
    class Data(
        val pot: Pot?,
        val currencies: List<String>,
        val defaultCurrency: String,
        val tags: List<TagState>
    ) :
        AddPotViewState()

    class Error(val error: String) : AddPotViewState()
    object Success : AddPotViewState()
    object Loading: AddPotViewState()
}

data class TagState(val name: String, val isApplied: Boolean)