package com.strv.dundee.model.api.bitfinex

import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Ticker
import java.util.ArrayList
import java.util.Date

class BitfinexTickerResponse : ArrayList<Double>() {
	fun getTicker(currency: String, coin: String) = Ticker(BitcoinSource.BITFINEX, currency, coin, get(9), get(1), get(4), Date().time)
}