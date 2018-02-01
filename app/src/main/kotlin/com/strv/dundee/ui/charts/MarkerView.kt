package com.strv.dundee.ui.charts

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.strv.dundee.R
import com.strv.dundee.model.entity.Currency

class MarkerView(context: Context, val currency: String) : MarkerView(context, R.layout.marker) {

	private val label: TextView = findViewById(R.id.tv_marker)
	private var customOffset: MPPointF = MPPointF(0.toFloat(), -height.toFloat())

	override fun refreshContent(e: Entry?, highlight: Highlight?) {
		label.text = Currency.formatValue(currency, e?.y?.toDouble())
		super.refreshContent(e, highlight)
	}

	override fun getOffset(): MPPointF {
		return customOffset
	}
}