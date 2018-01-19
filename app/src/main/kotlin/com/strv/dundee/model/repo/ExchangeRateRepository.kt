package com.strv.dundee.model.repo

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.api.exchangerate.ExchangeRateApi
import com.strv.dundee.model.api.exchangerate.ExchangeRateResponse
import com.strv.dundee.model.cache.ExchangeRateCache
import com.strv.dundee.model.entity.ExchangeRate
import com.strv.ktools.NetworkBoundResource
import com.strv.ktools.Resource
import com.strv.ktools.RetrofitCallLiveData
import com.strv.ktools.inject
import retrofit2.Response


class ExchangeRateRepository {

	val cache by inject<ExchangeRateCache>()

	private val exchangeRateApi by inject<ExchangeRateApi>()

	fun getExchangeRate(source: String, target: String, liveDataToReuse: LiveData<Resource<ExchangeRate>>? = null) = object : NetworkBoundResource<ExchangeRate, ExchangeRateResponse>(liveDataToReuse) {
		override fun saveCallResult(item: ExchangeRateResponse) {
			cache.putRate(item.getExchangeRate(source, target))
		}

		override fun shouldFetch(data: ExchangeRate?) = true

		override fun loadFromDb(): LiveData<ExchangeRate> = cache.getRate(source, target)

		override fun createCall(): LiveData<Response<out ExchangeRateResponse>> {
			return RetrofitCallLiveData(exchangeRateApi.getExchangeRate(source, target))
		}

	}.getAsLiveData()
}