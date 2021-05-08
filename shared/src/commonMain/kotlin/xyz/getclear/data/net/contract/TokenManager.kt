package xyz.getclear.data.net.contract

import xyz.getclear.data.data.Token

const val TOKEN = "secure_value"

interface TokenManager {
    fun getToken(): Token?
    fun setToken(token: Token?)
}