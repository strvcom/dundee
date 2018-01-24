package com.strv.dundee.model.api.exchangerate

import com.strv.dundee.model.entity.ExchangeRate
import com.strv.dundee.model.entity.ExchangeRates

data class ExchangeRateResponse(
		val base: String,
		val date: String,
		val rates: HashMap<String, Double>
){
	fun getExchangeRate(source: String, target: String) : ExchangeRate {
		return ExchangeRate(source, target, rates[target] ?: 1.toDouble())
	}

	fun getExchangeRates(source: String) : ExchangeRates {
		rates.put(source, 1.0)
		return ExchangeRates(source, rates)
	}
}