package xyz.getclear.data.net

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xyz.cleartrack.shared.Logger
import xyz.getclear.data.data.Pot
import xyz.getclear.data.net.contract.TokenManager

class PotApi(
    private val baseURL: String,
    private val client: HttpClient,
    private val tokenManager: TokenManager
) {
    suspend fun getAll() =
        client.get<List<Pot>> {
            url("${baseURL}data/pot/")
            header(HEADER_AUTH, "Token ${getToken()}")
        }

    suspend fun delete(id: String) {
        client.delete<String> {
            url("${baseURL}data/pot/$id/")
            header(HEADER_AUTH, "Token ${getToken()}")
        }
    }

    suspend fun add(pot: Pot) = client.post<Pot> {
        Logger.d("potapi", Json.encodeToString(pot))
        url("${baseURL}data/pot/")
        contentType(ContentType.Application.Json)
        header(HEADER_AUTH, "Token ${getToken()}")
        body = pot
    }

    suspend fun update(pot: Pot, id: String) = client.put<Pot> {
        Logger.d("potapi", Json.encodeToString(pot))
        url("${baseURL}data/pot/$id/")
        contentType(ContentType.Application.Json)
        header(HEADER_AUTH, "Token ${getToken()}")
        body = pot
    }

    private fun getToken() = tokenManager.getToken()!!.key
}