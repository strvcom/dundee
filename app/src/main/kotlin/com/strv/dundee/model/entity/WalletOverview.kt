package com.strv.dundee.model.entity

import android.arch.lifecycle.LiveData
import com.strv.ktools.Resource

data class WalletOverview(
		var coin: String,
		var amount: Double = 0.0,
		val boughtPrices: MutableList<Pair<String, Double>> = mutableListOf()
) {
	fun getBoughtPrice(currency: String?, exchangeRates: HashMap<String, LiveData<Resource<ExchangeRates>>>): Double =
		boughtPrices.sumByDouble { (exchangeRates[it.first]?.value?.data?.rates!![currency] ?: 0.0) * it.second }


	fun getProfit(currency: String, exchangeRates: HashMap<String, LiveData<Resource<ExchangeRates>>>, ticker: Ticker): Double =
		ticker.getValue(amount, currency, exchangeRates) - getBoughtPrice(currency, exchangeRates)

}
