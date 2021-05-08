package xyz.getclear.android.design.report

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import xyz.getclear.android.R
import java.util.*

class RiskChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int = 0
) : LineChart(context, attrs, defStyle) {
    init {
        // No Legend
        legend.isEnabled = false
        // No grid
        xAxis.setDrawGridLines(false)
        axisLeft.setDrawGridLines(false)
        axisRight.setDrawGridLines(true)
        axisRight.valueFormatter = CurrencyValueAxisFormatter()
        xAxis.valueFormatter = RiskLevelAxisFormatter()
        xAxis.setAvoidFirstLastClipping(true)
        axisRight.setDrawAxisLine(false)
        //No description
        description.text = ""
        //No right and left axis
        axisRight.isEnabled = true
        axisLeft.isEnabled = false

        //Show Bottom axis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor =
            ContextCompat.getColor(context, R.color.lineChartLegendTextColor)
        axisRight.textColor =
            ContextCompat.getColor(context, R.color.lineChartLegendTextColor)

        // Enable touch
        setTouchEnabled(false)
    }

    private val dataSets = ArrayList<ILineDataSet>()

    fun addDataSet(input: List<Float?>) {
        val entries = mutableListOf<Entry>()
        input.forEachIndexed { index, value ->
            Log.d("Risk-Chart", "$index has $value.toFloat()")
            value?.let { entries.add(Entry(index.toFloat(), it)) }
        }

        val dataSet = LineDataSet(entries, "Label")
        dataSet.color = ContextCompat.getColor(context, R.color.lineChartLineColor)

        dataSet.lineWidth = 2f
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        dataSet.setDrawFilled(true)
        dataSet.fillDrawable = ContextCompat.getDrawable(context, R.drawable.chart_fill)
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSets.add(dataSet)

        data = LineData(dataSets)
    }

    private class RiskLevelAxisFormatter : IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            return when {
                value < 0.5 -> "low risk"
                value > 2.5 -> "high risk"
                else -> ""
            }
        }
    }
}
