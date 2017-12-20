package com.strv.dundee.api.bitfinex

import com.strv.dundee.api.TickerProvider
import com.strv.dundee.model.BitcoinSource
import com.strv.dundee.model.Coin
import com.strv.dundee.model.Currency
import com.strv.dundee.model.Ticker
import java.util.*

class BitfinexTickerResponse : ArrayList<Double>(), TickerProvider {
    override fun getTicker(source: BitcoinSource, currency: Currency, coin: Coin): Ticker {
        return Ticker(source, currency, coin, get(9), get(1), get(4), Date().time)
    }
}