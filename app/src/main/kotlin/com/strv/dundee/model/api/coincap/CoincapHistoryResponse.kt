package com.strv.dundee.model.api.coincap

import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.entity.History
import com.strv.dundee.model.entity.HistoryPrice

data class CoincapHistoryResponse(
	val price: List<List<Double>>
) {
	fun getHistory(currency: String, coin: String) = History(BitcoinSource.COINCAP, Currency.USD, coin, price.map { HistoryPrice(it[0].toLong(), it[1]) })
}