package com.strv.dundee.model.repo

import android.arch.lifecycle.LiveData
import com.strv.dundee.app.Config
import com.strv.dundee.common.daysToNow
import com.strv.dundee.model.api.exchangerate.ExchangeRateApi
import com.strv.dundee.model.api.exchangerate.ExchangeRateResponse
import com.strv.dundee.model.cache.ExchangeRatesCache
import com.strv.dundee.model.entity.ExchangeRates
import com.strv.ktools.NetworkBoundResource
import com.strv.ktools.Resource
import com.strv.ktools.RetrofitCallLiveData
import com.strv.ktools.inject

class ExchangeRatesRepository {

	val cache by inject<ExchangeRatesCache>()

	private val exchangeRateApi by inject<ExchangeRateApi>()

	fun getExchangeRates(source: String, target: List<String>, liveDataToReuse: LiveData<Resource<ExchangeRates>>? = null) = object : NetworkBoundResource<ExchangeRates, ExchangeRateResponse>(liveDataToReuse) {
		override fun saveCallResult(item: ExchangeRateResponse) {
			cache.putRates(item.getExchangeRates(source))
		}

		override fun shouldFetch(data: ExchangeRates?) = (data?.date?.daysToNow() ?: 2) > Config.EXCHANGE_RATE_TTL_DAYS

		override fun loadFromDb(): LiveData<ExchangeRates> = cache.getRates(source)

		override fun createCall() = RetrofitCallLiveData(exchangeRateApi.getExchangeRates(source, target))

	}.getAsLiveData()
}