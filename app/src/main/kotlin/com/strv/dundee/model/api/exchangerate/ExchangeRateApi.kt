package com.strv.dundee.model.api.exchangerate

import com.strv.ktools.getRetrofitInterface
import retrofit2.Call

class ExchangeRateApi {
	val URL = "https://api.fixer.io/"

	val api = getRetrofitInterface(URL, ExchangeRateApiInterface::class.java)

	fun getExchangeRate(from: String, to: String): Call<out ExchangeResponse> {
		return api.getEchangeRate(from.toLowerCase(), to.toLowerCase())
	}
}