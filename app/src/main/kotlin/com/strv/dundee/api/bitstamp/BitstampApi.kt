package com.strv.dundee.api.bitstamp

import com.strv.dundee.api.TickerProvider
import com.strv.dundee.api.bitfinex.BitcoinApi
import com.strv.dundee.model.Coin
import com.strv.dundee.model.Currency
import com.strv.ktools.getRetrofitInterface
import retrofit2.Call

class BitstampApi : BitcoinApi {
    val URL = "https://www.bitstamp.net/api/v2/"

    val api = getRetrofitInterface(URL, BitstampApiInterface::class.java)

    override fun getTicker(coin: Coin, currency: Currency): Call<out TickerProvider> {
        return api.getTicker("${coin.name.toLowerCase()}${currency.name.toLowerCase()}")
    }
}