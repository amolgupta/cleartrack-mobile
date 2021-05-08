package xyz.cleartrack.shared

import platform.Foundation.NSUserDefaults
import xyz.getclear.data.data.Token
import xyz.getclear.data.net.contract.TOKEN
import xyz.getclear.data.net.contract.TokenManager

class TokenManagerImpl(private val preferences: NSUserDefaults) : TokenManager {
    override fun getToken(): Token? {
        return if (preferences.objectForKey(TOKEN) == null) {
            null
        } else {
            Token(preferences.objectForKey(TOKEN)!!.toString())
        }
    }

    override fun setToken(token: Token?) {
        preferences.setObject(token?.key, TOKEN)
    }
}