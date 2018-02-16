package com.strv.dundee.model.entity

import android.arch.persistence.room.Entity

@Entity(tableName = "history", primaryKeys = ["coin", "currency", "timeFrame"])
data class History(
	var coin: String = Coin.BTC,
	var currency: String = Currency.USD,
	var timeFrame: TimeFrame = TimeFrame.ALL,
	var prices: List<HistoryPrice> = listOf()
)