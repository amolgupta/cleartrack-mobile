package xyz.getclear.domain.di

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayAt
import org.koin.dsl.module
import xyz.getclear.domain.details.TransactionViewItemMapper
import xyz.getclear.domain.home.ChartMapper
import xyz.getclear.domain.home.GrowthMapper
import xyz.getclear.domain.home.PotsToTransactionListViewItemMapper
import xyz.getclear.domain.home.TrackedCurrenciesMapper
import xyz.getclear.domain.reports.mappers.GrowthReportMapper
import xyz.getclear.domain.reports.mappers.TrackedCurrenciesReportMapper
import xyz.getclear.domain.transactions.TransactionsToEntriesMapper
import xyz.getclear.vm.report.CurrencyReportAdapter
import xyz.getclear.vm.report.GrowthReportAdapter

val domainModule = module {
    single { Clock.System.todayAt(get()) }
    single { TimeZone.currentSystemDefault() }
    factory { TransactionsToEntriesMapper(get()) }
    factory { GrowthReportMapper(get()) }
    factory { ChartMapper(get(), get()) }
    factory { PotsToTransactionListViewItemMapper(get(), get()) }
    factory { TrackedCurrenciesReportMapper(get()) }
    factory { CurrencyReportAdapter(get()) }
    factory { GrowthReportAdapter(get()) }
    factory { TrackedCurrenciesMapper(get()) }
    factory { TransactionViewItemMapper(get(), get()) }
    factory { GrowthMapper(get(), get()) }
}
