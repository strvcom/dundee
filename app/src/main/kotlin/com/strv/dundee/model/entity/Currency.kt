package com.strv.dundee.model.entity

import java.text.NumberFormat
import java.util.*

object Currency {
	const val USD = "USD"
	const val EUR = "EUR"

	fun getAll() = arrayOf(Currency.USD, Currency.EUR)

	fun formatValue(currency: String?, value: Double?): String {
		if (value == null || currency == null) return ""
		val locale = when (currency) {
			USD -> Locale.US
			EUR -> Locale.GERMANY
			else -> Locale.US
		}
		return NumberFormat.getCurrencyInstance(locale).format(value)
	}
}