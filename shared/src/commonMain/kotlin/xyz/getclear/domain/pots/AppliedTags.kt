package xyz.getclear.domain.pots

import xyz.getclear.data.data.Pot
import xyz.getclear.vm.addPot.TagState

class AppliedTags {
    fun of(pot: Pot?, pots: List<Pot>) =
        pots
            .flatMap { it.tags }
            .toSet()
            .map {
                TagState(
                    it,
                    pot?.tags?.contains(it) ?: false
                )
            }
}