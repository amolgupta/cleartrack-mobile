package xyz.getclear.data.net

import xyz.getclear.data.data.User
import xyz.getclear.data.net.contract.TokenManager
import xyz.getclear.data.net.contract.UserStore
import kotlin.coroutines.cancellation.CancellationException

const val BASE_CURRENCY = "base_currency"
const val EMAIL_ID = "email_id"
const val SHOW_NOTIFICATIONS = "show_notifications"
const val USERNAME = "username"

class UserRepository(
    private val userApi: UserApi,
    private val tokenManager: TokenManager,
    private val userStore: UserStore
) {
    private var user: User? = null

    @Throws(UserError::class, CancellationException::class)
    suspend fun login(username: String,password: String) {
        val token = kotlin.runCatching { userApi.login(username, password) }
            .getOrElse { ex ->
                throw UserError("Unable to login", ex)
            }
        tokenManager.setToken(token)
    }

    @Throws(UserError::class, CancellationException::class)
    suspend fun logout() {
        kotlin.runCatching {
            userApi.logout(tokenManager.getToken()!!.key)
        }.getOrElse { ex ->
            throw UserError("Unable to logout", ex)
        }
        user = null
        tokenManager.setToken(null)
        userStore.clear()
    }

    @Throws(UserError::class, CancellationException::class)
    suspend fun register(
        username: String,
        email: String?,
        password1: String,
        password2: String
    ) {
        val token =
            kotlin.runCatching { userApi.register(username, email, password1, password2) }
                .getOrElse {
                    throw UserError("Unable to register", it)
                }
        tokenManager.setToken(token)
    }

    @Throws(UserError::class, CancellationException::class)
    suspend fun getUser(): User {
        user?.let { return it }

        val response =
            kotlin.runCatching { userApi.getUser(tokenManager.getToken()!!.key) }.getOrElse { ex ->
                throw UserError("Unable to get user", ex)
            }
        response.let {
            user = it
            userStore.putUser(it)
            return it
        }
    }
}

class UserError(override val message: String, e: Throwable?) : Throwable(message, e)

