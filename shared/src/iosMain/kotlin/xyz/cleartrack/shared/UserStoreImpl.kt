package xyz.cleartrack.shared

import platform.Foundation.NSUserDefaults
import xyz.getclear.data.data.User
import xyz.getclear.data.net.EMAIL_ID
import xyz.getclear.data.net.USERNAME
import xyz.getclear.data.net.contract.UserStore


class UserStoreImpl constructor(
    private val preferences: NSUserDefaults
) : UserStore {

    override fun putUser(user: User) {
        preferences.setObject(user.email, EMAIL_ID)
        preferences.setObject(user.username, USERNAME)
    }

    override fun clear() {
        preferences.removeObjectForKey(EMAIL_ID)
        preferences.removeObjectForKey(USERNAME)
    }
}