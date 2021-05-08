package xyz.getclear.data.net

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import xyz.getclear.data.data.Token
import xyz.getclear.data.data.User

const val HEADER_AUTH = "authorization"

class UserApi constructor(
    private val baseURL: String,
    private val client: HttpClient
) {
    suspend fun register(
        username: String,
        email: String? = null,
        password1: String,
        password2: String
    ) = client
        .post<Token> {
            url("${baseURL}auth/registration/")
            body = FormDataContent(io.ktor.http.Parameters.build {
                append("username", username)
                append("password1", password1)
                append("password2", password2)
                email?.let { append("email", it) }
            })
        }

    suspend fun login(
        username: String, password: String
    ) = client
        .post<Token> {
            url("${baseURL}auth/login/")
            body = FormDataContent(io.ktor.http.Parameters.build {
                append("username", username)
                append("password", password)
            })
        }

    suspend fun logout(token: String) = client.post<Any> {
        url("${baseURL}auth/logout/")
        header(HEADER_AUTH, "Token $token")
    }

    suspend fun getUser(token: String) = client.get<User> {
        url("${baseURL}auth/user/")
        header(HEADER_AUTH, "Token $token")
    }
}