package com.strv.dundee.model.repo

import com.strv.dundee.model.api.BitcoinApi
import com.strv.dundee.model.cache.BitcoinCache
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.NetworkBoundResource
import com.strv.ktools.ResourceLiveData

class TickerLiveData(val cache: BitcoinCache, val api: BitcoinApi) : ResourceLiveData<Ticker, Ticker>() {
	fun refresh(source: String, coin: String, currency: String) {
		setupResource(object : NetworkBoundResource.Callback<Ticker, Ticker> {
			override fun saveCallResult(item: Ticker) {
				cache.putTicker(item)
			}

			override fun shouldFetch(data: Ticker?) = true

			override fun loadFromDb() = cache.getTicker(source, currency, coin)

			override fun createCall() = api.getTicker(coin, currency)

		})
	}
}