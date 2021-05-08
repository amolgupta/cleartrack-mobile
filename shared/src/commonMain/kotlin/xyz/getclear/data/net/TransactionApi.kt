package xyz.getclear.data.net

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import xyz.getclear.data.data.Pot
import xyz.getclear.data.data.Transaction
import xyz.getclear.data.net.contract.TokenManager

class TransactionApi(
    private val baseURL: String,
    private val client: HttpClient,
    private val tokenManager: TokenManager
) {
    suspend fun delete(id: String) {
        client.delete<String> {
            url("${baseURL}data/transaction/$id/")
            header(HEADER_AUTH, "Token ${getToken()}")
        }
    }

    suspend fun add(transaction: Transaction) = client.post<Transaction> {
        url("${baseURL}data/transaction/")
        contentType(ContentType.Application.Json)
        header(HEADER_AUTH, "Token ${getToken()}")
        body = transaction
    }

    suspend fun update(
        transaction: Transaction,
        id: String
    ) = client.put<Pot> {
        url("${baseURL}data/transaction/$id")
        contentType(ContentType.Application.Json)
        header(HEADER_AUTH, "Token ${getToken()}")
        body = transaction
    }

    private fun getToken() = tokenManager.getToken()!!.key

}
