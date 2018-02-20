package com.strv.dundee.model.api.bitstamp

import android.app.Application
import android.arch.lifecycle.LiveData
import com.strv.dundee.app.Config
import com.strv.dundee.model.api.BitcoinApi
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.Resource
import com.strv.ktools.getRetrofitInterface
import com.strv.ktools.inject
import com.strv.ktools.mapResource

class BitstampApi : BitcoinApi {
	val application by inject<Application>()
	val config by inject<Config>()

	val URL = "https://www.bitstamp.net/api/v2/"

	val api = getRetrofitInterface(application, URL, BitstampApiInterface::class.java, config.HTTP_LOGGING_LEVEL)

	override fun getTicker(coin: String, currency: String): LiveData<Resource<Ticker>> {
		return api.getTicker("${coin.toLowerCase()}${currency.toLowerCase()}").mapResource({ it?.getTicker(currency, coin) })
	}
}