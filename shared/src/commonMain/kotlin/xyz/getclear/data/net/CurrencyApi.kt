package xyz.getclear.data.net

import io.ktor.client.*
import io.ktor.client.request.*

class CurrencyApi constructor(
    private val baseURL: String,
    private val client: HttpClient
) {
    suspend fun getCurrencies(token: String) = client.get<Map<String, Float>> {
        url("${baseURL}data/currency/")
        header(HEADER_AUTH, "Token $token")
    }
}