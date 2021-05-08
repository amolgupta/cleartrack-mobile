package xyz.getclear.domain.home

import kotlinx.datetime.LocalDate
import xyz.getclear.data.data.Pot
import xyz.getclear.data.net.CurrencyRepository
import xyz.getclear.data.utils.balance

class GrowthMapper constructor(
    val currencyRepository: CurrencyRepository,
    private val today: LocalDate
) {
    fun invoke(pots: List<Pot>, startDate: LocalDate): BalanceUiModel {
        var balanceToday = 0.0f
        var balanceAtStart = 0.0f

        pots.forEach { pot ->
            val fx = currencyRepository.getFxValue(pot.currency)
            balanceToday += fx * pot.balance(today)
            balanceAtStart += fx * pot.balance(startDate)
        }
        val growth = if (balanceAtStart.compareTo(0f) != 0) {
            ((balanceToday - balanceAtStart) / balanceAtStart * 100).toString()
        } else {
            null
        }

        return BalanceUiModel(
            value = balanceToday,
            dateLabel = "Since ${startDate.dayOfMonth} ${startDate.month.name.subSequence(0, 3)}",
            growth = growth
        )
    }
}

data class BalanceUiModel(
    val value: Float,
    val dateLabel: String,
    val growth: String?
)