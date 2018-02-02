package com.strv.dundee.model.repo

import com.strv.dundee.model.api.bitfinex.BitfinexApi
import com.strv.dundee.model.api.bitstamp.BitstampApi
import com.strv.dundee.model.cache.BitcoinCache
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.CandleSet
import com.strv.ktools.NetworkBoundResource
import com.strv.ktools.ResourceLiveData
import com.strv.ktools.inject

class CandlesLiveData : ResourceLiveData<CandleSet>() {
	val cache by inject<BitcoinCache>()
	val bitstampApi by inject<BitstampApi>()
	val bitfinexApi by inject<BitfinexApi>()

	fun refresh(source: String, coin: String, currency: String, timeFrame: String) {
		val api = when (source) {
			BitcoinSource.BITSTAMP -> bitstampApi
			BitcoinSource.BITFINEX -> bitfinexApi
			else -> bitstampApi
		}
		setupResource(object : NetworkBoundResource.Callback<CandleSet> {
			override fun saveCallResult(item: CandleSet) {
				cache.putCandles(item)
			}

			override fun shouldFetch(data: CandleSet?) = true

			override fun loadFromDb() = cache.getCandles(source, currency, coin, timeFrame)

			override fun createCall() = api.getCandles(coin, currency, timeFrame)
		})
	}
}