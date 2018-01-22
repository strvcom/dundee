package com.strv.dundee.model.api.exchangerate

import com.strv.dundee.model.entity.ExchangeRate

data class ExchangeRateResponse(
		val base: String,
		val date: String,
		val rates: HashMap<String, Double>
){
	fun getExchangeRate(source: String, target: String) : ExchangeRate {
		return ExchangeRate(source, target, rates[target] ?: 1.toDouble())
	}
}