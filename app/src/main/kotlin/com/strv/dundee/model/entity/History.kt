package com.strv.dundee.model.entity

import android.arch.persistence.room.Entity
import com.github.mikephil.charting.data.Entry

@Entity(tableName = "history", primaryKeys = ["coin", "currency", "timeFrame"])
data class History(
	var coin: String = Coin.BTC,
	var currency: String = Currency.USD,
	var timeFrame: TimeFrame = TimeFrame.ALL,
	var prices: List<HistoryPrice> = listOf()
) {

	fun getHistoricalProfit(walletOverview: WalletOverview, exchangeRates: ExchangeRates): List<Entry> {
		val result = mutableListOf<Entry>()
		val boughtPrice = walletOverview.getBoughtPrice(Currency.USD, exchangeRates)
		prices.forEach {
			if (walletOverview.firstWalletBoughtDate != null && it.timestamp > walletOverview.firstWalletBoughtDate!!.time)
				result.add(Entry(it.timestamp.toFloat(), (it.price * walletOverview.amount - boughtPrice).toFloat()))
		}

		var i = 0
		while (i < result.size - 1) {
			if ((result[i].y < 0 && result[i + 1].y > 0) || result[i].y > 0 && result[i + 1].y < 0) {
				val x = result[i].x + ((0 - result[i].y) * (result[i + 1].x - result[i].x)) / (result[i + 1].y - result[i].y)
				result.add(i + 1, Entry(x, 0f))
			}
			i++
		}

		return result
	}
}