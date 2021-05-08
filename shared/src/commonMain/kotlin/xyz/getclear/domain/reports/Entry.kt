package xyz.getclear.domain.reports

import kotlinx.datetime.LocalDate

data class Entry(
    val x: LocalDate,
    val y: Float
)

data class BarEntry(
    val x: Float,
    val y: Float,
    val data: Any
)