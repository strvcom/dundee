package com.strv.dundee.model.api.bitstamp

import com.strv.dundee.model.api.TickerProvider
import com.strv.dundee.model.entity.Ticker

data class BitstampTickerResponse(
		val last: String,
		val high: String,
		val low: String,
		val timestamp: String,
		val bid: String,
		val vwap: String,
		val volume: String,
		val ask: String,
		val open: String
) : TickerProvider {
	override fun getTicker(source: String, currency: String, coin: String) = Ticker(source, currency, coin, last.toDouble(), high.toDouble(), low.toDouble(), timestamp.toLong())
}