package com.strv.dundee.model.api.bitfinex

import android.app.Application
import android.arch.lifecycle.LiveData
import com.strv.dundee.model.api.BitcoinApi
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.Resource
import com.strv.ktools.getRetrofitInterface
import com.strv.ktools.inject
import com.strv.ktools.mapLiveData

class BitfinexApi : BitcoinApi {
	val application by inject<Application>()

	val URL = "https://api.bitfinex.com/v2/"

	val api = getRetrofitInterface(application, URL, BitfinexApiInterface::class.java)

	override fun getTicker(coin: String, currency: String): LiveData<Resource<Ticker>> {
		return api.getTicker("${coin.toUpperCase()}${currency.toUpperCase()}").mapLiveData({ it?.getTicker(currency, coin) })
	}
}