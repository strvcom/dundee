package com.strv.dundee.model.entity

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Entity
import com.strv.ktools.Resource

/*
Ticker object

Notes:
- data class needs to have default value so that it has empty constructor ready for Room
- properties also need to be vars because Room needs setters
 */
@Entity(tableName = "ticker", primaryKeys = ["source", "currency", "coin"])
data class Ticker(
	var source: String = BitcoinSource.BITSTAMP,
	var currency: String = Currency.USD,
	var coin: String = Coin.BTC,
	var lastPrice: Double = 0.0,
	var highPrice: Double = 0.0,
	var lowPrice: Double = 0.0,
	var timestamp: Long = 0
) {
	fun getValue(amount: Double, targetCurrency: String?, exchangeRates: HashMap<String, LiveData<Resource<ExchangeRates>>>): Double =
		amount * lastPrice * (exchangeRates[currency]?.value?.data?.rates!![targetCurrency] ?: 0.0)
}