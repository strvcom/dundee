package com.strv.dundee

import com.strv.dundee.base.TestDIModule
import com.strv.dundee.model.db.Converters
import com.strv.dundee.model.entity.HistoryPrice
import org.amshove.kluent.shouldEqual
import java.util.Date
import kotlin.test.BeforeTest
import kotlin.test.Test

class ConvertersTest {

	@BeforeTest
	fun setUp() {
		TestDIModule.initialize()
	}

	@Test
	fun dateFromTimestamp() {
		val converters = Converters()
		converters.fromTimestamp(1518018440000 ).toString() shouldEqual "Wed Feb 07 16:47:20 CET 2018"
	}

	@Test
	fun timestampToDate() {
		val converters = Converters()
		val date = Date(1518018440000)
		converters.dateToTimestamp(date) shouldEqual 1518018440000
	}

	@Test
	fun mapToString() {
		val converters = Converters()
		val map = HashMap<String, Double>()
		map["test"] = 23.0
		map["test1"] = -1.0
		converters.mapToString(map) shouldEqual "{\"test\":23.0,\"test1\":-1.0}"
	}

	@Test
	fun stringToMap() {
		val converters = Converters()
		val map = HashMap<String, Double>()
		map["test"] = 23.0
		map["test1"] = -1.0
		converters.stringToMap("{\"test\":23.0,\"test1\":-1.0}") shouldEqual map
	}

	@Test
	fun historyPriceListToString() {
		val converters = Converters()
		val list = mutableListOf<HistoryPrice>()
		list.add(HistoryPrice(123, 9.7))
		list.add(HistoryPrice(54, -98.9))
		converters.historyPriceListToString(list) shouldEqual "[{\"timestamp\":123,\"price\":9.7},{\"timestamp\":54,\"price\":-98.9}]"
	}

	@Test
	fun stringToHistoryPriceList() {
		val converters = Converters()
		val list = mutableListOf<HistoryPrice>()
		list.add(HistoryPrice(123, 9.7))
		list.add(HistoryPrice(54, -98.9))
		converters.stringToHistoryPriceList("[{\"timestamp\":123,\"price\":9.7},{\"timestamp\":54,\"price\":-98.9}]") shouldEqual list
	}

}