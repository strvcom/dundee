package com.strv.dundee.model.api.bitfinex

import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.History
import com.strv.dundee.model.entity.HistoryPrice
import java.util.ArrayList

class BitfinexCandlesResponse : ArrayList<List<Double>>() {
	fun getHistory(currency: String, coin: String) = History(BitcoinSource.BITFINEX, currency, coin, map { HistoryPrice(it[0].toLong(), (it[3] + it[4]) /2) })
}