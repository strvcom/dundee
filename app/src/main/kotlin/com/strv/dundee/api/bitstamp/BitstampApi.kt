package com.strv.dundee.api.bitstamp

import com.strv.ktools.getRetrofitInterface

class BitstampApi {
    val URL = "https://www.bitstamp.net/api/v2/"

    enum class Currency { USD, EUR }
    enum class Coin { BTC, XRP, LTC, ETH, BCH }


    val api = getRetrofitInterface(URL, BitstampApiInterface::class.java)

    fun getPrice(coin: Coin, currency: Currency) = api.getTicker("${coin.name.toLowerCase()}${currency.name.toLowerCase()}")

}