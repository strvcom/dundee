package com.strv.dundee.model.api.coincap

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.api.BitcoinApi
import com.strv.dundee.model.entity.CandleSet
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.getRetrofitInterface
import com.strv.ktools.mapLiveData
import retrofit2.Response

class CoincapApi : BitcoinApi {
	val URL = "http://coincap.io/"

	val api = getRetrofitInterface(URL, CoincapApiInterface::class.java)

	override fun getTicker(coin: String, currency: String): LiveData<Response<Ticker>> {
		return api.getTicker(coin.toUpperCase()).mapLiveData({ it?.getTicker(currency, coin) })
	}

	override fun getCandles(coin: String, currency: String, timeFrame: String): LiveData<Response<CandleSet>> {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}