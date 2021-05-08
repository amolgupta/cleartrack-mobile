package xyz.cleartrack.shared

expect class EmailValidator constructor() {
    fun isValid(email: String?): Boolean
}