package com.strv.dundee.common

import java.text.DecimalFormat

object NumberUtils {
	fun formatDecimal(number: Double, fraction: Int): String {
		val formater = DecimalFormat()
		formater.maximumFractionDigits = fraction
		return formater.format(number)
	}
}
