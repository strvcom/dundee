package com.strv.dundee.common

import java.text.DateFormat
import java.util.*


object DateFormatter {
	fun dateToString(date: Date?): String {
		return date?.let { DateFormat.getDateTimeInstance().format(date) } ?: ""
	}
}
