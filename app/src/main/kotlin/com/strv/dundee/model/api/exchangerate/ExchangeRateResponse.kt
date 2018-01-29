package com.strv.dundee.model.api.exchangerate

import com.strv.dundee.model.entity.ExchangeRates
import java.text.SimpleDateFormat

data class ExchangeRateResponse(
	val base: String,
	val date: String,
	val rates: HashMap<String, Double>
) {
	fun getExchangeRates(source: String): ExchangeRates {
		rates.put(source, 1.0)
		val dateFormat = SimpleDateFormat("yyy-MM-dd")
		return ExchangeRates(source, dateFormat.parse(date), rates)
	}
}