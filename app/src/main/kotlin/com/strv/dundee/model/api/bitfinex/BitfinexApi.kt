package com.strv.dundee.model.api.bitfinex

import com.strv.dundee.model.api.BitcoinApi
import com.strv.dundee.model.api.TickerProvider
import com.strv.ktools.getRetrofitInterface
import retrofit2.Call

class BitfinexApi : BitcoinApi {
	val URL = "https://api.bitfinex.com/v2/"

	val api = getRetrofitInterface(URL, BitfinexApiInterface::class.java)

	override fun getTicker(coin: String, currency: String): Call<out TickerProvider> {
		return api.getTicker("${coin.toUpperCase()}${currency.toUpperCase()}")
	}
}