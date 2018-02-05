package com.strv.dundee.model.api.coincap

import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.History
import com.strv.dundee.model.entity.HistroyPrice
import java.util.ArrayList

class CoincapHistoryResponse : ArrayList<List<Double>>() {
	fun getHistory(currency: String, coin: String) = History(BitcoinSource.BITFINEX, currency, coin, map { HistroyPrice(it[0].toLong(), it[1], it[2], it[3], it[4], it[5]) })
}