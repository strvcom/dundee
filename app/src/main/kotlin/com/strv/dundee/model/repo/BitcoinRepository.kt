package com.strv.dundee.model.repo

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.api.TickerProvider
import com.strv.dundee.model.api.bitfinex.BitfinexApi
import com.strv.dundee.model.api.bitstamp.BitstampApi
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Ticker
import com.strv.dundee.model.repo.common.NetworkBoundResource
import com.strv.dundee.model.repo.common.Resource
import com.strv.dundee.model.repo.common.RetrofitCallLiveData
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
}