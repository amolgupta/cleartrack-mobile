package xyz.cleartrack.shared

actual class EmailValidator actual constructor() {
    actual fun isValid(email: String?): Boolean {
        val emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
        email?.let {
            return Regex.fromLiteral(emailRegEx).matches(email)
        }
        return false
    }
}
