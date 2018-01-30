package com.strv.dundee.model.api.bitfinex

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.api.BitcoinApi
import com.strv.dundee.model.entity.CandleSet
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.getRetrofitInterface
import com.strv.ktools.mapLiveData
import retrofit2.Response

class BitfinexApi : BitcoinApi {
	val URL = "https://api.bitfinex.com/v2/"

	val api = getRetrofitInterface(URL, BitfinexApiInterface::class.java)

	override fun getTicker(coin: String, currency: String): LiveData<Response<Ticker>> {
		return api.getTicker("${coin.toUpperCase()}${currency.toUpperCase()}").mapLiveData({ it?.getTicker(currency, coin) })
	}

	override fun getCandles(coin: String, currency: String, timeFrame: String): LiveData<Response<CandleSet>> {
		return api.getCandles("${coin.toUpperCase()}${currency.toUpperCase()}", timeFrame).mapLiveData({ it?.getCandles(currency, coin, timeFrame) })
	}
}