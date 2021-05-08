package xyz.getclear.vm.auth

sealed class AuthCommand {
    data class Submit(val username: String, val password: String, val email: String, val acceptTerms: Boolean) : AuthCommand()
    object WhyEmail : AuthCommand()
    object TermsAndConditions : AuthCommand()
    data class ViewType(val viewType: PageViewType) : AuthCommand()
}


sealed class AuthViewState {
    class Error(
        val error: String? = null,
        val usernameError: String? = null,
        val emailError: String? = null,
        val passwordError: String? = null,
        val checkBoxError: Boolean = false
    ) : AuthViewState()

    data class ViewType(val viewType: PageViewType) : AuthViewState()
    object Success : AuthViewState()
    object EmailDialog : AuthViewState()
    object TermsAndConditions : AuthViewState()
}

enum class PageViewType {
    LOGIN, REGISTER
}