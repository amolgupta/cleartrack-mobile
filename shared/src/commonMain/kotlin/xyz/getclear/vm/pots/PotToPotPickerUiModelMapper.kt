package xyz.getclear.vm.pots

import xyz.getclear.data.data.Pot
import xyz.getclear.data.utils.currentBalance
import xyz.getclear.domain.common.Mapper
import xyz.getclear.domain.transactions.TransactionsToEntriesMapper

class PotToPotPickerUiModelMapper constructor(val mapper: TransactionsToEntriesMapper) :
    Mapper<Pot, PotPickerUiModel> {
    override fun invoke(from: Pot): PotPickerUiModel {
        return PotPickerUiModel(
            id = from.id!!,
            currency = from.currency,
            name = from.name,
            balance = from.currentBalance()
        )
    }
}