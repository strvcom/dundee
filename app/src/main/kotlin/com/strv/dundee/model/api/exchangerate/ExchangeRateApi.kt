package com.strv.dundee.model.api.exchangerate

import android.app.Application
import android.arch.lifecycle.LiveData
import com.strv.dundee.model.entity.ExchangeRates
import com.strv.ktools.Resource
import com.strv.ktools.getRetrofitInterface
import com.strv.ktools.inject
import com.strv.ktools.mapLiveData
import retrofit2.Call

class ExchangeRateApi {
	val application by inject<Application>()

	val URL = "https://api.fixer.io/"

	val api = getRetrofitInterface(application, URL, ExchangeRateApiInterface::class.java)

	fun getExchangeRate(source: String, target: String): Call<out ExchangeRateResponse> {
		return api.getExchangeRate(source.toUpperCase(), target.toUpperCase())
	}

	fun getExchangeRates(source: String, target: List<String>): LiveData<Resource<ExchangeRates>> {
		var targetString = ""
		target.forEach { targetString += "$it," }
		return api.getExchangeRate(source.toUpperCase(), targetString.substring(0, targetString.length - 1)).mapLiveData({ it?.getExchangeRates(source) })
	}
}