package com.strv.dundee.model.cache

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.db.HistoryDao
import com.strv.dundee.model.db.TickerDao
import com.strv.dundee.model.entity.History
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.inject
import com.strv.ktools.log

class BitcoinCache {
	private val tickerDao by inject<TickerDao>()
	private val historyDao by inject<HistoryDao>()

	fun getTicker(source: String, currency: String, coin: String): LiveData<Ticker> {
		val fromDb = tickerDao.getTicker(source, currency, coin)
		log("Reading ticker from db: ${fromDb.value}")
		return fromDb
	}

	fun putTicker(ticker: Ticker) {
		log("Saving ticker to db: $ticker")
		tickerDao.putTicker(ticker)
	}

	fun putHistory(history: History) {
		log("Saving history to db: $history")
		historyDao.putHistory(history)
	}

	fun getHistory(source: String, currency: String, coin: String): LiveData<History> {
		val fromDb = historyDao.getHistory(source, currency, coin)
		log("Reading history from db: ${fromDb.value}")
		return fromDb
	}
}