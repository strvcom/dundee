package com.strv.dundee.model.repo

import com.strv.dundee.app.Config
import com.strv.dundee.common.isOlderThan
import com.strv.dundee.model.api.exchangerate.ExchangeRateApi
import com.strv.dundee.model.api.exchangerate.ExchangeRateResponse
import com.strv.dundee.model.cache.ExchangeRatesCache
import com.strv.dundee.model.entity.ExchangeRates
import com.strv.ktools.NetworkBoundResource
import com.strv.ktools.ResourceLiveData
import com.strv.ktools.RetrofitCallLiveData
import java.util.Calendar

class ExchangeRatesLiveData(val cache: ExchangeRatesCache, val api: ExchangeRateApi) : ResourceLiveData<ExchangeRates, ExchangeRateResponse>() {
	fun refresh(source: String, target: List<String>) {
		setupResource(object : NetworkBoundResource.Callback<ExchangeRates, ExchangeRateResponse> {
			override fun saveCallResult(item: ExchangeRateResponse) {
				cache.putRates(item.getExchangeRates(source))
			}

			override fun shouldFetch(data: ExchangeRates?) = data?.date?.isOlderThan(Calendar.DAY_OF_YEAR, -Config.EXCHANGE_RATE_TTL_DAYS)
				?: true

			override fun loadFromDb() = cache.getRates(source)

			override fun createCall() = RetrofitCallLiveData(api.getExchangeRates(source, target))
		})
	}
}