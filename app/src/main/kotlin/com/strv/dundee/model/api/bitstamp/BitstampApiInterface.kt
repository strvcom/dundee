package com.strv.dundee.model.api.bitstamp

import android.arch.lifecycle.LiveData
import com.strv.ktools.Resource
import retrofit2.http.GET
import retrofit2.http.Path

interface BitstampApiInterface {

	@GET("ticker/{currencyPair}")
	fun getTicker(@Path("currencyPair") currencyPair: String): LiveData<Resource<BitstampTickerResponse>>
}
