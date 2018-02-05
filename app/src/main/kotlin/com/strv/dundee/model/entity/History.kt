package com.strv.dundee.model.entity

import android.arch.persistence.room.Entity

@Entity(tableName = "history", primaryKeys = ["source", "currency", "coin"])
data class History(
	var source: String = BitcoinSource.BITSTAMP,
	var currency: String = Currency.USD,
	var coin: String = Coin.BTC,
	var prices: List<HistroyPrice> = listOf()
)