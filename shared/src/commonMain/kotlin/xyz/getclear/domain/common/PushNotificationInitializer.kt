package xyz.getclear.domain.common

interface PushNotificationInitializer {
    fun initialize()
    fun setUserId(userId: String)
    fun removeUser()
}

interface NotificationHandler {
    fun notificationReceived(notification: Any?)
}