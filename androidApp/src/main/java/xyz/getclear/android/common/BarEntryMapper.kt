package xyz.getclear.android.common

import xyz.getclear.domain.reports.BarEntry


fun BarEntry.toChartEntry(): com.github.mikephil.charting.data.BarEntry {
    return com.github.mikephil.charting.data.BarEntry(
        this.x, this.y, this.data
    )
}