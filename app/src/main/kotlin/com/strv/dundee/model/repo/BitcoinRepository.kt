package com.strv.dundee.model.repo

import com.strv.dundee.model.api.bitfinex.BitfinexApi
import com.strv.dundee.model.api.bitstamp.BitstampApi
import com.strv.dundee.model.cache.BitcoinCache
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.ktools.inject

class BitcoinRepository {
	val cache by inject<BitcoinCache>()

	val bitstampApi by inject<BitstampApi>()
	val bitfinexApi by inject<BitfinexApi>()

	private fun getApi(source: String) = when (source) {
		BitcoinSource.BITSTAMP -> bitstampApi
		BitcoinSource.BITFINEX -> bitfinexApi
		else -> bitstampApi
	}

	fun getTicker(source: String, coin: String, currency: String) = TickerLiveData(cache, getApi(source)).apply { refresh(source, coin, currency) }

	fun getCandles(source: String, coin: String, currency: String, timeFrame: String) = CandlesLiveData(cache, getApi(source)).apply { refresh(source, coin, currency, timeFrame) }
}