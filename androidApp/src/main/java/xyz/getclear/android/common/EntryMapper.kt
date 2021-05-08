package xyz.getclear.android.common

import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import xyz.getclear.domain.reports.Entry

fun Entry.toChartEntry(): com.github.mikephil.charting.data.Entry {
    return com.github.mikephil.charting.data.Entry(
        this.x.atStartOfDayIn(TimeZone.UTC).epochSeconds.toFloat(), this.y
    )
}