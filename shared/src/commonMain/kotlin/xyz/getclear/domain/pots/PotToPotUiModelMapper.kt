package xyz.getclear.domain.pots

import xyz.getclear.data.data.Pot
import xyz.getclear.data.utils.currentBalance
import xyz.getclear.vm.potList.PotUiModel
import xyz.getclear.domain.common.Mapper
import xyz.getclear.domain.transactions.TransactionsToEntriesMapper

class PotToPotUiModelMapper constructor(val mapper: TransactionsToEntriesMapper) :
    Mapper<Pot, PotUiModel> {
    override fun invoke(from: Pot): PotUiModel {
        return PotUiModel(
            id = from.id!!,
            currency = from.currency,
            name = from.name,
            balance = from.currentBalance(),
            entries = mapper(from.transactions)
        )
    }
}