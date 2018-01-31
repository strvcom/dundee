package com.strv.dundee.model.repo

class BitcoinRepository {
	fun getTicker(source: String, coin: String, currency: String) = TickerLiveData().apply { refresh(source, coin, currency) }
	fun getCandles(source: String, coin: String, currency: String, timeFrame: String) = CandlesLiveData().apply { refresh(source, coin, currency, timeFrame) }
}