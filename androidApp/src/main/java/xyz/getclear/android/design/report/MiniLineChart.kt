package xyz.getclear.android.design.report

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import xyz.getclear.android.R

class MiniLineChart @JvmOverloads constructor(
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
        axisRight.setDrawGridLines(false)
        //No description
        description.text = ""
        //No right and left axis
        axisRight.isEnabled = false
        axisLeft.isEnabled = false
        xAxis.isEnabled = false

        // Enable touch
        setTouchEnabled(false)
    }

    fun draw(entries: List<Entry>) {

        val dataSet = LineDataSet(entries, "Label")
        dataSet.color = ContextCompat.getColor(context, R.color.lineChartLineColor)
        dataSet.lineWidth = 1.0f
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        val lineData = LineData(dataSet)
        data = lineData

        invalidate()
    }
}
