package com.strv.dundee.model.repo

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.api.BitcoinApi
import com.strv.dundee.model.api.TickerProvider
import com.strv.dundee.model.cache.BitcoinCache
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.NetworkBoundResource
import com.strv.ktools.ResourceLiveData
import com.strv.ktools.RetrofitCallLiveData
import retrofit2.Response

class TickerLiveData(val cache: BitcoinCache, val api: BitcoinApi) : ResourceLiveData<Ticker, TickerProvider>() {
	fun refresh(source: String, coin: String, currency: String) {
		setupResource(object : NetworkBoundResource.Callback<Ticker, TickerProvider> {
			override fun saveCallResult(item: TickerProvider) {
				cache.putTicker(item.getTicker(source, currency, coin))
			}

			override fun shouldFetch(data: Ticker?) = true

			override fun loadFromDb() = cache.getTicker(source, currency, coin)

			override fun createCall(): LiveData<Response<out TickerProvider>> {
				return RetrofitCallLiveData(api.getTicker(coin, currency))
			}
		})
	}
}