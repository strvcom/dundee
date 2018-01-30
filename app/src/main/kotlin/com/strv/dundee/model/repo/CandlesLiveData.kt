package com.strv.dundee.model.repo

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.api.BitcoinApi
import com.strv.dundee.model.api.CandlesProvider
import com.strv.dundee.model.cache.BitcoinCache
import com.strv.dundee.model.entity.CandleSet
import com.strv.ktools.NetworkBoundResource
import com.strv.ktools.ResourceLiveData
import com.strv.ktools.RetrofitCallLiveData
import retrofit2.Response

class CandlesLiveData(val cache: BitcoinCache, val api: BitcoinApi) : ResourceLiveData<CandleSet, CandlesProvider>() {
	fun setup(source: String, coin: String, currency: String, timeFrame: String) {
		setupResource(object : NetworkBoundResource.Callback<CandleSet, CandlesProvider> {
			override fun saveCallResult(item: CandlesProvider) {
				cache.putCandles(item.getCandles(source, currency, coin, timeFrame))
			}

			override fun shouldFetch(data: CandleSet?) = true

			override fun loadFromDb() = cache.getCandles(source, currency, coin, timeFrame)

			override fun createCall(): LiveData<Response<out CandlesProvider>> {
				// pick the right api
				return RetrofitCallLiveData(api.getCandles(coin, currency, timeFrame))
			}
		})
	}
}