package com.strv.dundee.base

import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.entity.ExchangeRates
import com.strv.dundee.model.entity.Ticker
import com.strv.dundee.model.entity.WalletOverview
import java.util.Date

object TestEntityProvider {

	fun getExchangeRatesEntity(): ExchangeRates {
		val rates = hashMapOf<String, Double>()
		rates[Currency.USD] = 1.0
		rates[Currency.CZK] = 20.5
		rates[Currency.EUR] = 0.8
		return ExchangeRates(Currency.USD, Date(), rates)
	}

	fun getTickerEntity(): Ticker {
		return Ticker(BitcoinSource.BITFINEX, Currency.USD, Coin.BTC, 5000.5, 5500.9, 4300.3, Date().time)
	}

	fun getWalletOverviewEntity(): WalletOverview {
		val boughtPrices = mutableListOf<Pair<String, Double>>()
		boughtPrices.add(Pair(Currency.CZK, 50000.5))
		boughtPrices.add(Pair(Currency.USD, 300.2))
		boughtPrices.add(Pair(Currency.EUR, 400.8))
		return WalletOverview(Coin.BTC, 10.5, boughtPrices)
	}

}