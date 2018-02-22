package com.strv.dundee.model.api.exchangerate

import android.app.Application
import android.arch.lifecycle.LiveData
import com.google.gson.Gson
import com.strv.dundee.app.Config
import com.strv.dundee.model.entity.ExchangeRates
import com.strv.ktools.Resource
import com.strv.ktools.getRetrofit
import com.strv.ktools.inject
import com.strv.ktools.mapResource

class ExchangeRateApi {
	val application by inject<Application>()
	val config by inject<Config>()
	val gson by inject<Gson>()

	val URL = "https://api.fixer.io/"

	val api = getRetrofit(application, URL, config.HTTP_LOGGING_LEVEL, gson = gson).create(ExchangeRateApiInterface::class.java)

	fun getExchangeRates(source: String, target: List<String>): LiveData<Resource<ExchangeRates>> {
		var targetString = ""
		target.forEach { targetString += "$it," }
		return api.getExchangeRate(source.toUpperCase(), targetString.substring(0, targetString.length - 1)).mapResource({ it?.getExchangeRates(source) })
	}
}