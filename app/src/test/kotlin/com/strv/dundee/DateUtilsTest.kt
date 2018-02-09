package com.strv.dundee

import com.strv.dundee.common.daysToNow
import com.strv.dundee.common.isOlderThan
import org.amshove.kluent.shouldEqual
import java.util.Calendar
import kotlin.test.Test

class DateUtilsTest {

	@Test
	fun daysToNow() {
		val expectedDiff = 3
		val calendar = Calendar.getInstance()
		calendar.add(Calendar.DAY_OF_YEAR, -expectedDiff)
		val date = calendar.time
		date.daysToNow() shouldEqual expectedDiff
	}

	@Test
	fun isOlderThan() {
		val calendar = Calendar.getInstance()
		calendar.add(Calendar.DAY_OF_YEAR, -2)
		val date = calendar.time
		date.isOlderThan(Calendar.HOUR, 24) shouldEqual true
	}

	@Test
	fun isOlderThan1() {
		val calendar = Calendar.getInstance()
		calendar.add(Calendar.DAY_OF_YEAR, -2)
		val date = calendar.time
		date.isOlderThan(Calendar.HOUR, 60) shouldEqual false
	}
}