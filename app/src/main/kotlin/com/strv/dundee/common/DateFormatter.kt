package com.strv.dundee.common

import java.text.DateFormat
import java.util.*


object DateFormatter {
	fun dateToString(date: Date?): String {
		var string = ""
		date.let { string = DateFormat.getDateTimeInstance().format(date) }
		return string
	}
}
