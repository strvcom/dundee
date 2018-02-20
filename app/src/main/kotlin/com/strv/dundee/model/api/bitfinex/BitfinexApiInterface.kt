package com.strv.dundee.model.api.bitfinex

import android.arch.lifecycle.LiveData
import com.strv.ktools.Resource
import retrofit2.http.GET
import retrofit2.http.Path

interface BitfinexApiInterface {

	@GET("ticker/t{currencyPair}")
	fun getTicker(@Path("currencyPair") currencyPair: String): LiveData<Resource<BitfinexTickerResponse>>
}

