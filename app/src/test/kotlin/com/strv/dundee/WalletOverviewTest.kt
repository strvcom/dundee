package com.strv.dundee

import com.strv.dundee.base.TestEntityProvider
import com.strv.dundee.model.entity.Currency
import org.amshove.kluent.shouldEqual
import kotlin.test.Test

class WalletOverviewTest {

	@Test
	fun getBoughtPrice() {
		val walletOverview = TestEntityProvider.getWalletOverviewEntity()
		walletOverview.getBoughtPrice(Currency.USD, TestEntityProvider.getExchangeRatesEntity()) shouldEqual 3240.2487804878047
	}

	@Test
	fun getBoughtPrice1() {
		val walletOverview = TestEntityProvider.getWalletOverviewEntity()
		walletOverview.getBoughtPrice(Currency.EUR, TestEntityProvider.getExchangeRatesEntity()) shouldEqual 2592.199024390244
	}

	@Test
	fun getBoughtPrice2() {
		val walletOverview = TestEntityProvider.getWalletOverviewEntity()
		walletOverview.getBoughtPrice(Currency.CZK, TestEntityProvider.getExchangeRatesEntity()) shouldEqual 66425.1
	}

	@Test
	fun getProfit() {
		val walletOverview = TestEntityProvider.getWalletOverviewEntity()
		walletOverview.getProfit(Currency.USD, TestEntityProvider.getExchangeRatesEntity(), TestEntityProvider.getTickerEntity()) shouldEqual 49265.001219512196
	}

	@Test
	fun getProfit1() {
		val walletOverview = TestEntityProvider.getWalletOverviewEntity()
		walletOverview.getProfit(Currency.EUR, TestEntityProvider.getExchangeRatesEntity(), TestEntityProvider.getTickerEntity()) shouldEqual 39412.00097560976
	}

	@Test
	fun getProfit2() {
		val walletOverview = TestEntityProvider.getWalletOverviewEntity()
		walletOverview.getProfit(Currency.CZK, TestEntityProvider.getExchangeRatesEntity(), TestEntityProvider.getTickerEntity()) shouldEqual 1009932.525
	}
}