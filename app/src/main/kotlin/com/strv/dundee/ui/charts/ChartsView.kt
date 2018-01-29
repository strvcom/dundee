package com.strv.dundee.ui.charts

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.strv.dundee.R
import com.strv.dundee.databinding.FragmentChartsBinding
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.ui.main.MainViewModel
import com.strv.ktools.Resource
import com.strv.ktools.vmb
import java.text.DateFormat
import java.util.Date

interface ChartsView {

}

class ChartsFragment : Fragment(), ChartsView {

	companion object {
		fun newInstance() = ChartsFragment().apply { arguments = Bundle() }
	}

	private val vmb by vmb<ChartsViewModel, FragmentChartsBinding>(R.layout.fragment_charts) { ChartsViewModel(ViewModelProviders.of(activity!!).get(MainViewModel::class.java)) }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = vmb.rootView

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		vmb.viewModel.candles?.observe(this, Observer {
			if (it?.status == Resource.Status.SUCCESS) {
				val entries = it.data?.candles?.map { Entry(it.timestamp.toFloat(), it.middle.toFloat()) }?.sortedBy { it.x }
				val btcDataSet = LineDataSet(entries, "${it.data?.currency}/${it.data?.coin}").apply {
					setDrawCircles(false)
					color = ContextCompat.getColor(activity!!, R.color.accent)
					lineWidth = resources.getDimensionPixelSize(R.dimen.spacing_1).toFloat() // 1dp
				}

				vmb.binding.chart.axisRight.setValueFormatter { value, axis -> Currency.formatValue(it.data?.currency, value.toDouble()) }

				val data = LineData(btcDataSet)
				vmb.binding.chart.setData(data)
				vmb.binding.chart.invalidate()
			}
		})
		vmb.binding.chart.axisLeft.isEnabled = false
		vmb.binding.chart.axisRight.apply {
			setDrawAxisLine(false)
		}
		vmb.binding.chart.description.isEnabled = false

		vmb.binding.chart.xAxis.apply {
			setValueFormatter { value, axis ->
				val timestamp = value.toLong()
				val date = Date(timestamp)
				DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)
			}
			labelCount = 4
			setDrawAxisLine(false)
		}
	}
}