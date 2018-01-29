package com.strv.dundee.model.repo

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.api.CandlesProvider
import com.strv.dundee.model.api.TickerProvider
import com.strv.dundee.model.api.bitfinex.BitfinexApi
import com.strv.dundee.model.api.bitstamp.BitstampApi
import com.strv.dundee.model.cache.BitcoinCache
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.CandleSet
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.NetworkBoundResource
import com.strv.ktools.Resource
import com.strv.ktools.RetrofitCallLiveData
import com.strv.ktools.inject
import retrofit2.Response

class BitcoinRepository {
	val cache by inject<BitcoinCache>()

	val bitstampApi by inject<BitstampApi>()
	val bitfinexApi by inject<BitfinexApi>()

	fun getTicker(source: String, coin: String, currency: String, liveDataToReuse: LiveData<Resource<Ticker>>? = null) = object : NetworkBoundResource<Ticker, TickerProvider>(liveDataToReuse) {
		override fun saveCallResult(item: TickerProvider) {
			cache.putTicker(item.getTicker(source, currency, coin))
		}

		override fun shouldFetch(data: Ticker?) = true

		override fun loadFromDb() = cache.getTicker(source, currency, coin)

		override fun createCall(): LiveData<Response<out TickerProvider>> {
			// pick the right api
			val api = when (source) {
				BitcoinSource.BITSTAMP -> bitstampApi
				BitcoinSource.BITFINEX -> bitfinexApi
				else -> bitstampApi
			}
			return RetrofitCallLiveData(api.getTicker(coin, currency))
		}
	}.getAsLiveData()

	fun getCandles(source: String, coin: String, currency: String, timeFrame: String, liveDataToReuse: LiveData<Resource<CandleSet>>? = null) = object : NetworkBoundResource<CandleSet, CandlesProvider>(liveDataToReuse) {
		override fun saveCallResult(item: CandlesProvider) {
			cache.putCandles(item.getCandles(source, currency, coin, timeFrame))
		}

		override fun shouldFetch(data: CandleSet?) = true

		override fun loadFromDb() = cache.getCandles(source, currency, coin, timeFrame)

		override fun createCall(): LiveData<Response<out CandlesProvider>> {
			// pick the right api
			val api = when (source) {
				BitcoinSource.BITSTAMP -> bitstampApi
				BitcoinSource.BITFINEX -> bitfinexApi
				else -> bitstampApi
			}
			return RetrofitCallLiveData(api.getCandles(coin, currency, timeFrame))
		}
	}.getAsLiveData()
}