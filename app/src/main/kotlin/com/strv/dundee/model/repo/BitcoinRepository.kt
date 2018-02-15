package com.strv.dundee.model.repo

import com.strv.dundee.model.entity.TimeFrame

class BitcoinRepository {
	fun getTicker(source: String, coin: String, currency: String) = TickerLiveData().apply { refresh(source, coin, currency) }
	fun getHistory(coin: String, timeFrame: TimeFrame) = HistoryLiveData().apply { refresh(coin, timeFrame) }
}