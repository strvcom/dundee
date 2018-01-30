package com.strv.dundee.model.repo

import com.strv.dundee.model.api.BitcoinApi
import com.strv.dundee.model.cache.BitcoinCache
import com.strv.dundee.model.entity.CandleSet
import com.strv.ktools.NetworkBoundResource
import com.strv.ktools.ResourceLiveData

class CandlesLiveData(val cache: BitcoinCache, val api: BitcoinApi) : ResourceLiveData<CandleSet, CandleSet>() {
	fun refresh(source: String, coin: String, currency: String, timeFrame: String) {
		setupResource(object : NetworkBoundResource.Callback<CandleSet, CandleSet> {
			override fun saveCallResult(item: CandleSet) {
				cache.putCandles(item)
			}

			override fun shouldFetch(data: CandleSet?) = true

			override fun loadFromDb() = cache.getCandles(source, currency, coin, timeFrame)

			override fun createCall() = api.getCandles(coin, currency, timeFrame)
		})
	}
}