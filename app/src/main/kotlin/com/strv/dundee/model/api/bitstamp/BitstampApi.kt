package com.strv.dundee.model.api.bitstamp

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.strv.dundee.model.api.BitcoinApi
import com.strv.dundee.model.entity.History
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.RetrofitResponse
import com.strv.ktools.getRetrofitInterface
import com.strv.ktools.mapLiveData

class BitstampApi : BitcoinApi {
	val URL = "https://www.bitstamp.net/api/v2/"

	val api = getRetrofitInterface(URL, BitstampApiInterface::class.java)

	override fun getTicker(coin: String, currency: String): LiveData<RetrofitResponse<Ticker>> {
		return api.getTicker("${coin.toLowerCase()}${currency.toLowerCase()}").mapLiveData({ it?.getTicker(currency, coin) })
	}

	override fun getHistory(coin: String, currency: String): LiveData<RetrofitResponse<History>> {
		return MutableLiveData()
	}
}