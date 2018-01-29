package com.strv.dundee.model.entity

import android.arch.persistence.room.Entity

@Entity(tableName = "candleSet", primaryKeys = ["source", "currency", "coin", "timeFrame"])
data class CandleSet(
	var source: String = BitcoinSource.BITSTAMP,
	var currency: String = Currency.USD,
	var coin: String = Coin.BTC,
	var timeFrame: String = "",
	var candles: List<Candle> = listOf()
)