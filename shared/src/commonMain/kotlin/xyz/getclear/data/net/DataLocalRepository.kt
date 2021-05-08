package xyz.getclear.data.net

import xyz.getclear.data.data.Pot

class DataLocalRepository {
    private var isStale: Boolean = true
    private var pots: MutableList<Pot> = mutableListOf()

    fun getAllPots() = pots

    fun isStale() = isStale

    fun getPot(id: String) = pots.findLast { it.id == id }

    fun markStale() {
        isStale = true
    }

    fun setPots(pots: List<Pot>) {
        this.pots.clear()
        this.pots.addAll(pots)
        isStale = false
    }
}