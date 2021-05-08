package xyz.cleartrack.shared

import android.util.Patterns
import java.util.regex.Pattern

actual class EmailValidator actual constructor(){
    private val emailPattern: Pattern = Patterns.EMAIL_ADDRESS
    actual fun isValid(email: String?) =
        !(email.isNullOrBlank() || emailPattern.matcher(email).matches())
}
