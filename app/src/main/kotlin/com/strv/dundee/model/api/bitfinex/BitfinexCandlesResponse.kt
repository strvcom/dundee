package com.strv.dundee.model.api.bitfinex

import com.strv.dundee.model.api.CandlesProvider
import com.strv.dundee.model.entity.Candle
import com.strv.dundee.model.entity.CandleSet
import java.util.ArrayList

class BitfinexCandlesResponse : ArrayList<List<Double>>(), CandlesProvider {
	override fun getCandles(source: String, currency: String, coin: String, timeFrame: String) = CandleSet(source, currency, coin, timeFrame, map { Candle(it[0].toLong(), it[1], it[2], it[3], it[4], it[5]) })
}