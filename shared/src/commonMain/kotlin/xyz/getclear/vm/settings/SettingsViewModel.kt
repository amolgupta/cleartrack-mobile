package xyz.getclear.vm.settings

import dev.icerock.moko.mvvm.livedata.MediatorLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import xyz.getclear.data.data.Currencies
import org.koin.core.component.inject
import xyz.getclear.data.data.User
import xyz.getclear.data.net.CurrencyRepository
import xyz.getclear.data.net.UserError
import xyz.getclear.data.net.UserRepository
import xyz.getclear.data.net.contract.DataRepository
import xyz.getclear.domain.common.GravatarUrlGenerator
import xyz.getclear.domain.common.PushNotificationInitializer

class SettingsViewModel : ViewModel(), KoinComponent {
    private val userRepository: UserRepository by inject()
    private val currencyRepository: CurrencyRepository by inject()
    private val dataRepository: DataRepository by inject()
    private val gravatarUrlGenerator: GravatarUrlGenerator by inject()
    private val scope: CoroutineScope by inject()
    private val pushNotificationInitializer: PushNotificationInitializer by inject()

    val viewState = MediatorLiveData<SettingsViewState>(SettingsViewState.Data()).apply {
        value = SettingsViewState.Data()
    }

    fun logout() {
        scope.launch {
            try {
                userRepository.logout()
                dataRepository.markStale()
                pushNotificationInitializer.removeUser()
                viewState.postValue(SettingsViewState.RestartEvent)
            } catch (e: UserError) {
                viewState.postValue(SettingsViewState.Error(e.message))
            }
        }
    }

    fun start() {
        scope.launch {
            try {
                postViewState(userRepository.getUser(), currencyRepository.getCurrencies())
            } catch (e: UserError) {
                viewState.postValue(SettingsViewState.Error(e.message))
            }
        }
    }

    private fun postViewState(
        user: User,
        currencies: Currencies
    ) {
        val image = user.email?.let { gravatarUrlGenerator.getUrl(it) }
        val subscriptionEndDate = user.subscription_ends.toString()

        viewState.postValue(
            SettingsViewState.Data(
                email = user.email,
                username = user.username,
                image = image,
                subscriptionEndDate = subscriptionEndDate,
                currencies = currencies
            )
        )
    }
}