package xyz.getclear.android.di

import android.content.Context
import android.util.Patterns
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.squareup.picasso.Picasso
import io.ktor.client.engine.*
import io.ktor.client.engine.android.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import xyz.getclear.android.analytics.AnalyticsWrapperImpl
import xyz.getclear.android.common.*
import xyz.getclear.android.login.TokenManagerImpl
import xyz.getclear.android.settings.GravatarUrlGeneratorImpl
import xyz.getclear.android.settings.Md5Util
import xyz.getclear.data.net.DefaultCurrencyProvider
import xyz.getclear.data.net.contract.NetworkConnectivityUseCase
import xyz.getclear.data.net.contract.TokenManager
import xyz.getclear.data.net.contract.UserStore
import xyz.getclear.domain.common.AnalyticsWrapper
import xyz.getclear.domain.common.GravatarUrlGenerator
import xyz.getclear.domain.common.NotificationHandler
import xyz.getclear.domain.common.PushNotificationInitializer
import xyz.getclear.domain.pots.AppliedTags
import xyz.getclear.domain.pots.PotToPotUiModelMapper
import xyz.getclear.vm.addPot.AddPotViewModel
import xyz.getclear.vm.addTransaction.AddTransactionViewModel
import xyz.getclear.vm.appStart.AppStartViewModel
import xyz.getclear.vm.auth.AuthViewModel
import xyz.getclear.vm.details.DetailsViewModel
import xyz.getclear.vm.home.HomeViewModel
import xyz.getclear.vm.potList.PotsViewModel
import xyz.getclear.vm.potPicker.PotPickerViewModel
import xyz.getclear.vm.pots.PotToPotPickerUiModelMapper
import xyz.getclear.vm.report.ReportViewModel
import xyz.getclear.vm.settings.SettingsViewModel
import kotlin.coroutines.CoroutineContext

const val ENCRYPTED_PREFS = "encrypted_shared_prefs"

val appModule = module {
    factory<Picasso> { Picasso.get() }
    factory { AndroidEngineConfig() }
    factory<UserStore> { UserStoreImpl(get()) }
    factory<DefaultCurrencyProvider> { DefaultCurrencyProviderImpl(get()) }
    single(named(ENCRYPTED_PREFS)) {
        EncryptedSharedPreferences.create(
            androidContext(),
            "token_info",
            MasterKey.Builder(androidContext()).setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    single<TokenManager> { TokenManagerImpl(get(named(ENCRYPTED_PREFS))) }

    single<HttpClientEngine> { AndroidClientEngine(config = get()) }

    single { Md5Util() }
    single<GravatarUrlGenerator> { GravatarUrlGeneratorImpl(get()) }

    single { androidContext().getSharedPreferences(SETTINGS_PREF_NAME, Context.MODE_PRIVATE) }

    single { Patterns.EMAIL_ADDRESS }
    single<AnalyticsWrapper> { AnalyticsWrapperImpl(androidContext()) }
    single<CoroutineContext> { Dispatchers.Unconfined }
    single { CoroutineScope(get()) }
    // Mappers
    factory { OneSignalNotificationReceivedHandler(get(), get()) }
    factory<NetworkConnectivityUseCase> { NetworkConnectivityUseCaseImpl(get()) }
    factory { PotToPotUiModelMapper(get()) }
    factory { PotToPotPickerUiModelMapper(get()) }
    factory<PushNotificationInitializer> { PushNotificationInitializerImpl(get()) }
    factory<NotificationHandler> { OneSignalNotificationReceivedHandler(get(), get()) }
    factory { AppliedTags() }
    // ViewModels
    viewModel { AuthViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { ReportViewModel() }
    viewModel { DetailsViewModel() }
    viewModel { HomeViewModel() }
    viewModel { AddTransactionViewModel() }
    viewModel { PotsViewModel() }
    viewModel { AddPotViewModel() }
    viewModel { AppStartViewModel() }
    viewModel { PotPickerViewModel() }


}
