package xyz.cleartrack.shared

import io.ktor.client.engine.ios.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults
import xyz.getclear.data.di.dataModule
import xyz.getclear.data.net.DefaultCurrencyProvider
import xyz.getclear.data.net.contract.TokenManager
import xyz.getclear.data.net.contract.UserStore
import xyz.getclear.domain.common.AnalyticsWrapper
import xyz.getclear.domain.di.domainModule
import xyz.getclear.vm.vmModule
import kotlin.coroutines.CoroutineContext

fun initKoinIos() = startKoin {
    modules(iosModule, dataModule, domainModule, vmModule )
}

val iosModule = module {
    factory<AnalyticsWrapper> { AnalyticsWrapperImpl() }
    single<CoroutineContext> { Dispatchers.Unconfined }
    single { CoroutineScope(get()) }
    single { Ios.create() }

    single<TokenManager> { TokenManagerImpl(NSUserDefaults.standardUserDefaults()) }
    single<UserStore> { UserStoreImpl(NSUserDefaults.standardUserDefaults()) }
    single<DefaultCurrencyProvider> { DefaultCurrencyProviderImpl(NSUserDefaults.standardUserDefaults()) }
}
