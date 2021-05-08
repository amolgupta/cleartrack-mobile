package xyz.getclear.android.common

import android.content.SharedPreferences
import xyz.getclear.data.data.User
import xyz.getclear.data.net.EMAIL_ID
import xyz.getclear.data.net.USERNAME
import xyz.getclear.data.net.contract.UserStore

const val SETTINGS_PREF_NAME = "user_Settings"

class UserStoreImpl constructor(
    private val preferences: SharedPreferences
) : UserStore {

    override fun putUser(user: User) {
        val editor = preferences.edit()
        editor.putString(EMAIL_ID, user.email)
        editor.putString(USERNAME, user.username)
        editor.apply()
    }

    override fun clear() {
        preferences.edit().clear().apply()
    }
}