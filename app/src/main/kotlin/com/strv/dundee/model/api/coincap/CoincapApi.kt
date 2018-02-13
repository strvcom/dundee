package com.strv.dundee.model.api.coincap

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.api.BitcoinApi
import com.strv.dundee.model.entity.History
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.RetrofitResponse
import com.strv.ktools.getRetrofitInterface
import com.strv.ktools.mapLiveData

class CoincapApi : BitcoinApi {
	val URL = "http://coincap.io/"

	val api = getRetrofitInterface(URL, CoincapApiInterface::class.java)

	override fun getTicker(coin: String, currency: String): LiveData<RetrofitResponse<Ticker>> {
		return api.getTicker(coin.toUpperCase()).mapLiveData({ it?.getTicker(currency, coin) })
	}

	override fun getHistory(coin: String, currency: String): LiveData<RetrofitResponse<History>> {
		return api.getHistory(coin).mapLiveData({ it?.getHistory(currency, coin)})
	}
}