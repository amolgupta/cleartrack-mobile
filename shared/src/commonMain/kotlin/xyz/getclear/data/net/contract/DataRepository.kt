package xyz.getclear.data.net.contract

import xyz.getclear.data.data.Pot
import xyz.getclear.data.data.Transaction

interface DataRepository {
    suspend fun getAllPots(): List<Pot>
    suspend fun getPot(id: String): Pot?
    suspend fun deletePot(id: String)
    suspend fun deleteTransaction(id: String)
    suspend fun addPot(item: Pot)
    suspend fun updatePot(item: Pot)
    suspend fun updateTransaction(item: Transaction)
    suspend fun addTransaction(item: Transaction)
    fun markStale()
}