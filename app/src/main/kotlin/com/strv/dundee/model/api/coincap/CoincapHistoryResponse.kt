package com.strv.dundee.model.api.coincap

import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.entity.History
import com.strv.dundee.model.entity.HistoryPrice
import com.strv.dundee.model.entity.TimeFrame

data class CoincapHistoryResponse(
	val price: List<List<Double>>
) {
	fun getHistory(coin: String, timeFrame: TimeFrame) = History(coin, Currency.USD, timeFrame, price.map { HistoryPrice(it[0].toLong(), it[1]) })
}