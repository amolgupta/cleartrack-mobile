package xyz.getclear.vm.appStart

sealed class AppStartEvents {
    class Error(val error: String) : AppStartEvents()
    object Loading : AppStartEvents()
    object Success : AppStartEvents()
    object AuthError : AppStartEvents()
    object UpdateRequired : AppStartEvents()
    object NetworkError : AppStartEvents()
}