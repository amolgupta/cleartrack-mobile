package xyz.cleartrack.shared

expect class Logger {
    companion object {
        fun i(tag: String? = null, message: String)
        fun d(tag: String? = null, message: String)
        fun w(tag: String? = null, message: String)
        fun e(tag: String? = null, message: String)
    }
}