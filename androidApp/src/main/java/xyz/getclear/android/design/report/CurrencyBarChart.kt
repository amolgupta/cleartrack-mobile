package xyz.getclear.android.design.report

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import xyz.getclear.android.R

class CurrencyBarChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int = 0
) : BarChart(context, attrs, defStyle) {
    init {
        legend.isEnabled = false
        xAxis.setDrawGridLines(false)
        axisLeft.setDrawGridLines(false)
        axisRight.setDrawGridLines(false)
        axisLeft.setDrawZeroLine(false)
        axisRight.setDrawZeroLine(false)
        axisLeft.setDrawLabels(false)
        axisRight.setDrawLabels(false)
        xAxis.setDrawLabels(false)
        xAxis.isEnabled = false
        axisRight.isEnabled = false
        axisLeft.isEnabled = false
        description.text = ""
        axisRight.valueFormatter = CurrencyValueAxisFormatter()
        xAxis.axisMinimum = 0f
        axisRight.axisMinimum = 0f
        axisLeft.axisMinimum = 0f
    }

    fun bind(entries: List<BarEntry>, color: Int) {
        val dataSet = BarDataSet(entries, "")
        dataSet.color = color
        val data = BarData(dataSet)
        data.setValueTextSize(12f)
        data.setValueFormatter(LabelFormatter())
        data.setValueTextColor(
            ContextCompat.getColor(
                context,
                R.color.lineChartLegendTextColor
            )
        )
        this.data = data
        highlightValues(null)
        invalidate()
    }

    class LabelFormatter : IValueFormatter {
        override fun getFormattedValue(
            value: Float,
            entry: Entry?,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler?
        ): String = entry?.data.toString()
    }
}