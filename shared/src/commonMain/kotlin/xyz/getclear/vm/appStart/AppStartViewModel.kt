package xyz.getclear.vm.appStart

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.getclear.data.data.User
import xyz.getclear.data.net.CurrencyRepository
import xyz.getclear.data.net.UserError
import xyz.getclear.data.net.UserRepository
import xyz.getclear.data.net.contract.NetworkConnectivityUseCase
import xyz.getclear.domain.common.PushNotificationInitializer

class AppStartViewModel: KoinComponent {
    private val userRepository: UserRepository by inject()
    private val currencyRepository: CurrencyRepository by inject()
    private val scope: CoroutineScope by inject()
    private val pushNotificationInitializer: PushNotificationInitializer by inject()
    private val networkConnectivityUseCase: NetworkConnectivityUseCase by inject()


    private val eventChannel = Channel<AppStartEvents>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    // App start sequence
    fun start() {
        scope.launch {
            try {
                if (networkConnectivityUseCase.isNetworkAvailable()) {
                    val user = userRepository.getUser()
                    initPush(user)
                    currencyRepository.getCurrencies()
                    sendEvent(AppStartEvents.Success)
                    initOneSignal()
                } else {
                    sendEvent(AppStartEvents.NetworkError)
                }
            } catch (e: UserError) {
                sendEvent(AppStartEvents.AuthError)
            } catch (e: Exception) {
                sendEvent(AppStartEvents.Error(e.message?:"Error"))
            }
        }
    }

    private fun initPush(user: User) {
        pushNotificationInitializer.setUserId(user.username)
    }

    private fun initOneSignal() {
        pushNotificationInitializer.initialize()
    }

    private fun sendEvent(event: AppStartEvents){
        scope.launch {
            eventChannel.send(event)
        }
    }
}