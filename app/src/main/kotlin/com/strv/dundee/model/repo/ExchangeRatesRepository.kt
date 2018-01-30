package com.strv.dundee.model.repo

import com.strv.dundee.model.api.exchangerate.ExchangeRateApi
import com.strv.dundee.model.cache.ExchangeRatesCache
import com.strv.ktools.inject

class ExchangeRatesRepository {
	private val cache by inject<ExchangeRatesCache>()
	private val exchangeRateApi by inject<ExchangeRateApi>()

	fun getExchangeRates(source: String, target: List<String>) = ExchangeRatesLiveData(cache, exchangeRateApi).apply { refresh(source, target) }
}