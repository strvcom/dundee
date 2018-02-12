package com.strv.dundee

import com.strv.dundee.base.TestEntityProvider
import com.strv.dundee.model.entity.Currency
import org.amshove.kluent.shouldEqual
import kotlin.test.Test

class ExchangeRatesTest {

	@Test
	fun calculate() {
		val exchangeRates = TestEntityProvider.getExchangeRatesEntity()
		exchangeRates.calculate(Currency.USD, Currency.USD, 10.5) shouldEqual 10.5
	}

	@Test
	fun calculate1() {
		val exchangeRates = TestEntityProvider.getExchangeRatesEntity()
		exchangeRates.calculate(Currency.USD, Currency.EUR, 10.5) shouldEqual 8.4
	}

	@Test
	fun calculate2() {
		val exchangeRates = TestEntityProvider.getExchangeRatesEntity()
		exchangeRates.calculate(Currency.USD, Currency.CZK, 10.5) shouldEqual 215.25
	}

	@Test
	fun calculate3() {
		val exchangeRates = TestEntityProvider.getExchangeRatesEntity()
		exchangeRates.calculate(Currency.EUR, Currency.USD, 10.5) shouldEqual 13.125
	}

	@Test
	fun calculate4() {
		val exchangeRates = TestEntityProvider.getExchangeRatesEntity()
		exchangeRates.calculate(Currency.EUR, Currency.EUR, 10.5) shouldEqual 10.5
	}

	@Test
	fun calculate5() {
		val exchangeRates = TestEntityProvider.getExchangeRatesEntity()
		exchangeRates.calculate(Currency.EUR, Currency.CZK, 10.5) shouldEqual 269.0625
	}

	@Test
	fun calculate6() {
		val exchangeRates = TestEntityProvider.getExchangeRatesEntity()
		exchangeRates.calculate(Currency.CZK, Currency.USD, 10.5) shouldEqual 0.5121951219512195
	}

	@Test
	fun calculate7() {
		val exchangeRates = TestEntityProvider.getExchangeRatesEntity()
		exchangeRates.calculate(Currency.CZK, Currency.EUR, 10.5) shouldEqual 0.4097560975609756
	}

	@Test
	fun calculate8() {
		val exchangeRates = TestEntityProvider.getExchangeRatesEntity()
		exchangeRates.calculate(Currency.CZK, Currency.CZK, 10.5) shouldEqual 10.5
	}
}