package xyz.getclear.android.login

import android.content.SharedPreferences
import xyz.getclear.data.data.Token
import xyz.getclear.data.net.contract.TOKEN
import xyz.getclear.data.net.contract.TokenManager

class TokenManagerImpl(
    private val preferences: SharedPreferences
) : TokenManager {

    override fun getToken(): Token? {
        return if (preferences.getString(TOKEN, null) == null) {
            null
        } else {
            Token(preferences.getString(TOKEN, null)!!)
        }
    }

    override fun setToken(token: Token?) {
        preferences.edit().putString(TOKEN, token?.key).apply()
    }
}
