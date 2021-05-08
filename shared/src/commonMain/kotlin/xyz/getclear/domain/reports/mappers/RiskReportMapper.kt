package xyz.getclear.domain.reports.mappers

import xyz.getclear.data.data.Pot
import xyz.getclear.data.net.CurrencyRepository
import xyz.getclear.data.utils.currentBalance
import xyz.getclear.domain.common.Mapper

@Suppress("EXPERIMENTAL_API_USAGE")
class RiskMapper constructor(val currencyRepository: CurrencyRepository) :
    Mapper<List<Pot>, Array<Float?>> {
    override fun invoke(from: List<Pot>): Array<Float?> {
        return if (from.isEmpty()) {
            arrayOfNulls(0)
        } else {
            val output: Array<Float?> = arrayOfNulls(4)
            output.fill(0f)

            from.forEach {
                var temp = output[it.riskLevel / 3]
                temp = temp?.plus(it.currentBalance() * currencyRepository.getFxValue(it.currency))

                output[it.riskLevel / 3] = temp
            }
            output
        }
    }
}