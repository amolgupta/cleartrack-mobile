package xyz.getclear.android.design.report

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import kotlin.math.ln
import kotlin.math.pow

class CurrencyValueAxisFormatter : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        if (value < 1000) return "" + value
        val exp = (ln(value.toDouble()) / ln(1000.0)).toInt()
        return String.format(
            "%.1f %c",
            value / 1000.0.pow(exp.toDouble()),
            "kMGTPE"[exp - 1]
        )
    }
}