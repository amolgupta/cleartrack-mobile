package xyz.getclear.vm.appStart

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.getclear.data.data.User
import xyz.getclear.data.net.CurrencyRepository
import xyz.getclear.data.net.UserError
import xyz.getclear.data.net.UserRepository
import xyz.getclear.data.net.contract.NetworkConnectivityUseCase
import xyz.getclear.domain.common.PushNotificationInitializer

class AppStartViewModel: ViewModel(), KoinComponent {
    private val userRepository: UserRepository by inject()
    private val currencyRepository: CurrencyRepository by inject()
    private val scope: CoroutineScope by inject()
    private val pushNotificationInitializer: PushNotificationInitializer by inject()
    private val networkConnectivityUseCase: NetworkConnectivityUseCase by inject()

    private val _viewState = MutableLiveData<AppStartViewState>(AppStartViewState.Loading)
    val viewState: LiveData<AppStartViewState> = _viewState

    private val _networkError = MutableLiveData<Unit?>(null)
    val networkError: LiveData<Unit?> = _networkError

    // App start sequence
    fun start() {
        _viewState.postValue(AppStartViewState.Loading)
        scope.launch {
            try {
                if (networkConnectivityUseCase.isNetworkAvailable()) {
                    val user = userRepository.getUser()
                    initPush(user)
                    currencyRepository.getCurrencies()
                    _viewState.postValue(AppStartViewState.Success)
                    initOneSignal()
                } else {
                    _networkError.postValue(Unit)
                }
            } catch (e: UserError) {
                _viewState.postValue(AppStartViewState.AuthError)
            } catch (e: Exception) {
                _viewState.postValue(AppStartViewState.Error(e.message?:"Error"))
            }
        }
    }

    private fun initPush(user: User) {
        pushNotificationInitializer.setUserId(user.username)
    }

    private fun initOneSignal() {
        pushNotificationInitializer.initialize()
    }
}