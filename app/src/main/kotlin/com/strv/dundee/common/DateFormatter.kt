package com.strv.dundee.common

import java.text.DateFormat
import java.util.Date

object DateFormatter {
	fun dateToString(date: Date?): String {
		return date?.let { DateFormat.getDateTimeInstance().format(date) } ?: ""
	}
}
