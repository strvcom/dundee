package com.strv.dundee.model.repo

import com.strv.dundee.app.Config
import com.strv.dundee.common.daysToNow
import com.strv.dundee.model.api.exchangerate.ExchangeRateApi
import com.strv.dundee.model.cache.ExchangeRatesCache
import com.strv.dundee.model.entity.ExchangeRates
import com.strv.ktools.NetworkBoundResource
import com.strv.ktools.ResourceLiveData

class ExchangeRatesLiveData(val cache: ExchangeRatesCache, val api: ExchangeRateApi) : ResourceLiveData<ExchangeRates, ExchangeRates>() {
	fun refresh(source: String, target: List<String>) {
		setupResource(object : NetworkBoundResource.Callback<ExchangeRates, ExchangeRates> {
			override fun saveCallResult(item: ExchangeRates) {
				cache.putRates(item)
			}

			override fun shouldFetch(data: ExchangeRates?) = (data?.date?.daysToNow() ?: 2) > Config.EXCHANGE_RATE_TTL_DAYS

			override fun loadFromDb() = cache.getRates(source)

			override fun createCall() = api.getExchangeRates(source, target)
		})
	}
}