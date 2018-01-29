package com.strv.dundee.model.cache

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.db.CandleSetDao
import com.strv.dundee.model.db.TickerDao
import com.strv.dundee.model.entity.CandleSet
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.inject
import com.strv.ktools.log

class BitcoinCache {
	private val tickerDao by inject<TickerDao>()
	private val candleSetDao by inject<CandleSetDao>()

	fun getTicker(source: String, currency: String, coin: String): LiveData<Ticker> {
		val fromDb = tickerDao.getTicker(source, currency, coin)
		log("Reading ticker from db: ${fromDb.value}")
		return fromDb
	}

	fun putTicker(ticker: Ticker) {
		log("Saving ticker to db: $ticker")
		tickerDao.putTicker(ticker)
	}

	fun putCandles(candleSet: CandleSet) {
		log("Saving candleSet to db: $candleSet")
		candleSetDao.putCandleSet(candleSet)
	}

	fun getCandles(source: String, currency: String, coin: String, timeFrame: String): LiveData<CandleSet> {
		val fromDb = candleSetDao.getCandleSet(source, currency, coin, timeFrame)
		log("Reading candleSet from db: ${fromDb.value}")
		return fromDb
	}
}