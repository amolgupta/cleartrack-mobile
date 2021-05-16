package xyz.getclear.vm.auth

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.cleartrack.shared.EmailValidator
import xyz.getclear.data.net.CurrencyRepository
import xyz.getclear.data.net.UserError
import xyz.getclear.data.net.UserRepository
import xyz.getclear.domain.common.*

class AuthViewModel : ViewModel(), KoinComponent {
    private val userRepository: UserRepository by inject()
    private val scope: CoroutineScope by inject()
    private val analyticsWrapper: AnalyticsWrapper by inject()
    private val emailValidator =  EmailValidator()
    private val currencyRepository: CurrencyRepository by inject()

    private val _viewState = MutableStateFlow<AuthViewState>(AuthViewState.ViewType(viewType = PageViewType.REGISTER))
    val viewState : StateFlow<AuthViewState> = _viewState

    private var isLogin = false

    init {
        updateState(AuthViewState.ViewType(PageViewType.REGISTER))
    }

    private fun login(username: String, password: String) {
        scope.launch {
            try {
                userRepository.login(username, password)
                currencyRepository.getCurrencies()
                updateState(AuthViewState.Success)
                analyticsWrapper.logEvent(EVENT_LOGIN_SUCCESS)
            } catch (e: UserError) {
                updateState(AuthViewState.Error(e.message))
            }
        }
    }

    private fun register(username: String, email: String?, password1: String, password2: String) {
        scope.launch {
            try {
                userRepository.register(
                    username,
                    email,
                    password1,
                    password2
                )
                currencyRepository.getCurrencies()
                updateState(AuthViewState.Success)
                analyticsWrapper.logEvent(EVENT_REGISTER_SUCCESS)
            } catch (e: UserError) {
                updateState(AuthViewState.Error(e.message))
            }
        }
    }

    fun dispatch(command: AuthCommand) = when (command) {
        is AuthCommand.Submit -> {
            if (isLogin) {
                if (isLoginInputValid(
                        username = command.username,
                        password = command.password
                    )
                ) {
                    login(
                        username = command.username,
                        password = command.password
                    )
                }
                analyticsWrapper.logEvent(EVENT_LOGIN_CLICKED)
            } else {
                if (isRegisterInputValid(
                        username = command.username,
                        password = command.password,
                        email = command.email,
                        checkboxRegister = command.acceptTerms
                    )
                )
                    register(
                        username = command.username,
                        password1 = command.password,
                        password2 = command.password,
                        email = command.email
                    )
                analyticsWrapper.logEvent(EVENT_REGISTER_CLICKED)
            }
        }
        is AuthCommand.WhyEmail -> updateState(AuthViewState.EmailDialog)
        is AuthCommand.TermsAndConditions -> updateState(AuthViewState.TermsAndConditions)
        is AuthCommand.ViewType -> {
            isLogin = command.viewType == PageViewType.LOGIN
            updateState(AuthViewState.ViewType(command.viewType))
        }
    }

    private fun isLoginInputValid(username: String, password: String): Boolean {
        var isError = false
        var userError: String? = null
        var passwordError: String? = null
        if (username.length < 4) {
            userError = "Minimum 4 characters required"
            isError = true
        }
        if (password.length < 6) {
            passwordError = "Minimum 6 characters required"
            isError = true
        }
        if (isError) {
            updateState(
                AuthViewState.Error(
                    usernameError = userError,
                    passwordError = passwordError
                )
            )
        }
        return !isError
    }

    private fun isRegisterInputValid(
        username: String,
        email: String?,
        password: String,
        checkboxRegister: Boolean
    ): Boolean {
        var isError = false
        var userError: String? = null
        var emailError: String? = null
        var passwordError: String? = null
        if (username.length < 4) {
            userError = "Minimum 4 characters required"
            isError = true
        }
        if (password.length < 6) {
            passwordError = "Minimum 6 characters required"
            isError = true
        }
        if (!emailValidator.isValid(email)) {
            emailError = "Invalid Email"
            isError = true
        }
        if (isError || !checkboxRegister) {
            isError = true
            updateState(
                AuthViewState.Error(
                    usernameError = userError,
                    passwordError = passwordError,
                    emailError = emailError,
                    checkBoxError = !checkboxRegister
                )
            )
        }
        return !isError
    }

    private fun updateState(state: AuthViewState){
        _viewState.value = state
    }
}