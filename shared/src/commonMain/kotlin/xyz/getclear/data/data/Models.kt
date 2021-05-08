package xyz.getclear.data.data

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayAt
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.getclear.data.utils.DateSerializer

const val DEFAULT_CURRENCY = "USD"

@Serializable
data class Pot @OptIn(InternalSerializationApi::class)
constructor(
    @SerialName("uuid")
    val id: String? = null,
    val name: String,
    val currency: String = DEFAULT_CURRENCY,
    val riskLevel: Int = 0,
    val lockIn: Int = 0,
    val tags: List<String>,
    val transactions: MutableList<Transaction> = mutableListOf()
)

@Serializable
data class Transaction @OptIn(InternalSerializationApi::class) constructor(
    @SerialName("uuid")
    val id: String? = null,
    val amount: Float,
    @Serializable(with = DateSerializer::class) val date: LocalDate? = Clock.System.todayAt(TimeZone.currentSystemDefault()),
    val pot: String,
    val usdAmount: Float = 0f
)

@Serializable
data class User  @OptIn(InternalSerializationApi::class) constructor(
    val username: String,
    val email: String?,
    @Serializable(with = DateSerializer::class) val subscription_ends: LocalDate
)

@Serializable
data class Token(val key: String)

typealias Currencies = Map<String, Float>
typealias Currency = Pair<String, Float>
