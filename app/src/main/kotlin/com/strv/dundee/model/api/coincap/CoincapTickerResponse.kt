package com.strv.dundee.model.api.coincap

import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.entity.Ticker
import java.util.Date

data class CoincapTickerResponse(
	val price_eur: Double,
	val price_usd: Double
) {
	fun getTicker(currency: String, coin: String) = Ticker(BitcoinSource.COINCAP, currency, coin, getPrice(currency), getPrice(currency), getPrice(currency), Date().time)

	private fun getPrice(currency: String) = when (currency) {
		Currency.USD -> price_usd
		Currency.EUR -> price_eur
		else -> 0.0
	}
}