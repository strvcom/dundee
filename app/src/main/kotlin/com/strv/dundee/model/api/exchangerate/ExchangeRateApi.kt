package com.strv.dundee.model.api.exchangerate

import com.strv.ktools.getRetrofitInterface
import retrofit2.Call

class ExchangeRateApi {
	val URL = "https://api.fixer.io/"

	val api = getRetrofitInterface(URL, ExchangeRateApiInterface::class.java)

	fun getExchangeRate(source: String, target: String): Call<out ExchangeRateResponse> {
		return api.getExchangeRate(source.toUpperCase(), target.toUpperCase())
	}

	fun getExchangeRates(source: String, target: List<String>): Call<out ExchangeRateResponse> {
		var targetString = ""
		target.forEach { targetString += "$it," }
		return api.getExchangeRate(source.toUpperCase(), targetString.substring(0, targetString.length - 1))
	}
}