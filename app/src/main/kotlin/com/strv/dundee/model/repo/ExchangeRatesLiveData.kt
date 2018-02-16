package com.strv.dundee.model.repo

import com.strv.dundee.app.Config
import com.strv.dundee.common.daysToNow
import com.strv.dundee.model.api.exchangerate.ExchangeRateApi
import com.strv.dundee.model.cache.ExchangeRatesCache
import com.strv.dundee.model.entity.ExchangeRates
import com.strv.ktools.NetworkBoundResource
import com.strv.ktools.ResourceLiveData
import com.strv.ktools.inject

class ExchangeRatesLiveData : ResourceLiveData<ExchangeRates>() {
	private val cache by inject<ExchangeRatesCache>()
	private val api by inject<ExchangeRateApi>()

	fun refresh(source: String, target: List<String>) {
		setupCached(object : NetworkBoundResource.Callback<ExchangeRates> {
			override fun saveCallResult(item: ExchangeRates) {
				cache.putRates(item)
			}

			override fun shouldFetch(dataFromCache: ExchangeRates?) = (dataFromCache?.date?.daysToNow() ?: 2) > Config.EXCHANGE_RATE_TTL_DAYS

			override fun loadFromDb() = cache.getRates(source)

			override fun createNetworkCall() = api.getExchangeRates(source, target)
		})
	}
}