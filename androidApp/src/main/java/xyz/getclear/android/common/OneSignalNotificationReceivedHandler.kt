package xyz.getclear.android.common

import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import xyz.getclear.data.net.contract.DataRepository
import xyz.getclear.domain.common.NotificationHandler
import xyz.getclear.domain.common.PushNotificationInitializer

class OneSignalNotificationReceivedHandler constructor(
    private val dataRepository: DataRepository,
    private val scope: CoroutineScope
) : NotificationHandler {
    override fun notificationReceived(notification: Any?) {
        scope.launch {
            dataRepository.markStale()
            dataRepository.getAllPots()
        }
    }
}

class PushNotificationInitializerImpl(
    private val handler: OneSignalNotificationReceivedHandler
) : PushNotificationInitializer {

    override fun setUserId(userId: String) {
        OneSignal.setExternalUserId(userId)
    }

    override fun initialize() {
        OneSignal.OSNotificationOpenedHandler { handler }
    }

    override fun removeUser() {
        OneSignal.removeExternalUserId()
    }
}