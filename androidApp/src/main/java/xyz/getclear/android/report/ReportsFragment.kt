package xyz.getclear.android.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import xyz.getclear.android.R
import xyz.getclear.android.common.ViewBindingHolder
import xyz.getclear.android.common.ViewBindingHolderImpl
import xyz.getclear.android.common.toChartEntry
import xyz.getclear.android.databinding.FragmentReportsBinding
import xyz.getclear.vm.report.ReportViewModel
import org.koin.android.ext.android.inject

class ReportsFragment : Fragment(),
    ViewBindingHolder<FragmentReportsBinding> by ViewBindingHolderImpl() {

    private val viewModel: ReportViewModel by inject()
    private var uiStateJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        initBinding(FragmentReportsBinding.inflate(layoutInflater), this) {
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiStateJob = lifecycleScope.launch {

            viewModel.viewState.collect { state ->
                requireBinding {
                    reportCurrency.currencyReportChart.bind(
                        state.currencyReport.map { it.toChartEntry() },
                        ContextCompat.getColor(requireContext(), R.color.lineChartLineColor)
                    )
                    reportRisk.riskReportChart.addDataSet(
                        state.riskReport
                    )
                    reportGrowth.growthReportChart.addDataSet(state.growthReport)
                }
            }
        }
    }

    override fun onStop() {
        uiStateJob?.cancel()
        super.onStop()
    }
}
