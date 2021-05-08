package xyz.cleartrack.shared

actual class Logger {
    actual companion object {
        actual fun i(tag: String?, message: String) {
            print(message)
        }

        actual fun d(tag: String?, message: String) {
            print(message)
        }

        actual fun w(tag: String?, message: String) {
            print(message)
        }

        actual fun e(tag: String?, message: String) {
            print(message)
        }
    }
}