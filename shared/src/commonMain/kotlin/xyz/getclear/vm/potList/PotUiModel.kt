package xyz.getclear.vm.potList

import xyz.getclear.domain.reports.Entry

data class PotUiModel(
    val id: String,
    val name: String,
    val balance: Float,
    val currency: String,
    val entries : List<Entry>
)