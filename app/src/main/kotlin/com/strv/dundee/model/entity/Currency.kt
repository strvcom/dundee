package com.strv.dundee.model.entity

import java.text.NumberFormat
import java.util.Locale

object Currency {
	const val USD = "USD"
	const val EUR = "EUR"
	const val CZK = "CZK"

	fun getAll() = arrayOf(Currency.USD, Currency.EUR, Currency.CZK)

	fun formatValue(currency: String?, value: Double?): String {
		if (value == null || currency == null) return ""
		val locale = when (currency) {
			USD -> Locale.US
			EUR -> Locale.GERMANY
			CZK -> Locale("cs", "CZ")
			else -> Locale.US
		}
		return NumberFormat.getCurrencyInstance(locale).format(value)
	}
}