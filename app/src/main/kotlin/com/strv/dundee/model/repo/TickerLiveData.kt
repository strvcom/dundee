package com.strv.dundee.model.repo

import com.strv.dundee.model.api.bitfinex.BitfinexApi
import com.strv.dundee.model.api.bitstamp.BitstampApi
import com.strv.dundee.model.api.coincap.CoincapApi
import com.strv.dundee.model.cache.BitcoinCache
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.NetworkBoundResource
import com.strv.ktools.ResourceLiveData
import com.strv.ktools.inject

class TickerLiveData : ResourceLiveData<Ticker, Ticker>() {
	val cache by inject<BitcoinCache>()
	val bitstampApi by inject<BitstampApi>()
	val bitfinexApi by inject<BitfinexApi>()
	val coincapApi by inject<CoincapApi>()

	fun refresh(source: String, coin: String, currency: String) {
		val api = when (source) {
			BitcoinSource.BITSTAMP -> bitstampApi
			BitcoinSource.BITFINEX -> bitfinexApi
			BitcoinSource.COINCAP -> coincapApi
			else -> bitstampApi
		}

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