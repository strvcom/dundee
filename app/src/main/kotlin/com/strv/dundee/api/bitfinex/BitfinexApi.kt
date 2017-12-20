package com.strv.dundee.api.bitfinex

import com.strv.dundee.api.TickerProvider
import com.strv.dundee.model.Coin
import com.strv.dundee.model.Currency
import com.strv.ktools.getRetrofitInterface
import retrofit2.Call

class BitfinexApi : BitcoinApi {
    val URL = "https://api.bitfinex.com/v2/"

    val api = getRetrofitInterface(URL, BitfinexApiInterface::class.java)

    override fun getTicker(coin: Coin, currency: Currency): Call<out TickerProvider> {
        return api.getTicker("${coin.name.toUpperCase()}${currency.name.toUpperCase()}")
    }
}