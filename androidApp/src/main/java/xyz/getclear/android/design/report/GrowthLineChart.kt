package xyz.getclear.android.design.report

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import xyz.getclear.android.R
import xyz.getclear.android.common.toChartEntry
import java.util.*

class GrowthLineChart @JvmOverloads constructor(
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

        xAxis.valueFormatter = IAxisValueFormatter { value, _ ->
            val date = Instant.fromEpochSeconds(value.toLong()).toLocalDateTime(TimeZone.UTC)
            return@IAxisValueFormatter "${date.month.name.substring(0, 3)} ${date.year}"
        }
        axisRight.valueFormatter = CurrencyValueAxisFormatter()
        xAxis.setAvoidFirstLastClipping(true)
        axisRight.setDrawAxisLine(false)
        //No description
        description.text = ""
        //No right and left axis
        axisRight.isEnabled = true
        axisLeft.isEnabled = false
        xAxis.labelRotationAngle = 270f
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

    fun addDataSet(entries: List<xyz.getclear.domain.reports.Entry>) {

        val dataSet = LineDataSet(entries.map { it.toChartEntry() }, "Label")
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
}
