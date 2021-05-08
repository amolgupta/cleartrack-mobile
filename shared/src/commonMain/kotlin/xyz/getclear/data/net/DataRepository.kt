package xyz.getclear.data.net

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xyz.getclear.data.data.Pot
import xyz.getclear.data.data.Transaction
import xyz.getclear.data.net.contract.DataRepository

class DataRepositoryImpl constructor(
    private val potApi: PotApi,
    private val transactionApi: TransactionApi,
    private val dataLocalRepository: DataLocalRepository
) : DataRepository {

    override fun markStale() {
        dataLocalRepository.markStale()
    }

    override suspend fun getAllPots(): List<Pot> {
        return if (!dataLocalRepository.isStale()) {
            dataLocalRepository.getAllPots()
        } else {
            val response = potApi.getAll()
            response.let { it1 ->
                dataLocalRepository.setPots(it1.toMutableList())
            }
            response
        }
    }

    override suspend fun deletePot(id: String) {
        dataLocalRepository.markStale()
        potApi.delete(id)
    }

    override suspend fun deleteTransaction(
        id: String
    ) {
        dataLocalRepository.markStale()
        transactionApi.delete(id)
    }

    override suspend fun addPot(item: Pot) {
        dataLocalRepository.markStale()
        val response = potApi.add(item)
        print(response)
    }

    override suspend fun addTransaction(
        item: Transaction
    ) {
        dataLocalRepository.markStale()
        Json.encodeToString(item)
        transactionApi.add(item)
    }

    override suspend fun updatePot(item: Pot) {
        dataLocalRepository.markStale()
        potApi.update(item, item.id!!)
    }

    override suspend fun updateTransaction(item: Transaction) {
        dataLocalRepository.markStale()
        transactionApi.update(item, item.id!!)
    }

    override suspend fun getPot(id: String): Pot? {
        return if (dataLocalRepository.isStale()) {
            getAllPots().find { it.id == id }
        } else {
            dataLocalRepository.getPot(id)
        }
    }
}
