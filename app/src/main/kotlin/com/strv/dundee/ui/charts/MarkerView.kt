package com.strv.dundee.ui.charts

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.strv.dundee.R
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.entity.ExchangeRates

class MarkerView(context: Context, private val sourceCurrency: String, private val targetCurrency: String, private val exchangeRates: ExchangeRates) : MarkerView(context, R.layout.marker) {

	private val label: TextView = findViewById(R.id.tv_marker)
	private var customOffset: MPPointF = MPPointF(0.toFloat(), -height.toFloat())

	override fun refreshContent(e: Entry?, highlight: Highlight?) {
		label.text = Currency.formatValue(targetCurrency, exchangeRates.calculate(sourceCurrency, targetCurrency, e?.y?.toDouble()))
		super.refreshContent(e, highlight)
	}

	override fun getOffset(): MPPointF {
		return customOffset
	}
}