package com.strv.dundee

import com.strv.dundee.base.TestEntityProvider
import org.amshove.kluent.shouldEqual
import kotlin.test.Test

class TickerTest {

	@Test
	fun getValue() {
		val ticker = TestEntityProvider.getTickerEntity()
		ticker.getValue(10.5) shouldEqual 52505.25
	}
}