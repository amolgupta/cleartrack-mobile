package xyz.getclear.vm.appStart

sealed class AppStartViewState {
    class Error(val error: String) : AppStartViewState()
    object Loading : AppStartViewState()
    object Success : AppStartViewState()
    object AuthError : AppStartViewState()
    object UpdateRequired : AppStartViewState()
}