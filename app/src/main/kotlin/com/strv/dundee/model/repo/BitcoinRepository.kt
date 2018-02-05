package com.strv.dundee.model.repo

class BitcoinRepository {
	fun getTicker(source: String, coin: String, currency: String) = TickerLiveData().apply { refresh(source, coin, currency) }
	fun getHistory(source: String, coin: String, currency: String) = HistoryLiveData().apply { refresh(source, coin, currency) }
}