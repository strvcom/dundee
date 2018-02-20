package com.strv.dundee.common

import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

object DateUtils {
	fun dateToString(date: Date?): String {
		return date?.let { DateFormat.getDateTimeInstance().format(date) } ?: ""
	}

	fun dateToDayString(date: Date?): String {
		return date?.let { DateFormat.getDateInstance().format(date) } ?: ""
	}
}

fun Date.isOlderThan(calendarUnit: Int, value: Int) = this.before(Calendar.getInstance().apply { add(calendarUnit, -value) }.time)

fun Date.daysToNow(): Int = TimeUnit.MILLISECONDS.toDays(Calendar.getInstance().timeInMillis - Calendar.getInstance().apply { time = this@daysToNow }.timeInMillis).toInt()
