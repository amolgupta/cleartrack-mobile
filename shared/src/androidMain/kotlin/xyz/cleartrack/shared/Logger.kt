package xyz.cleartrack.shared

import android.util.Log

actual class Logger {
    actual companion object {
        actual fun i(tag: String?, message: String) {
            Log.i(tag, message)
        }

        actual fun d(tag: String?, message: String) {
            Log.d(tag, message)
        }

        actual fun w(tag: String?, message: String) {
            Log.w(tag, message)
        }

        actual fun e(tag: String?, message: String) {
            Log.e(tag, message)
        }
    }
}