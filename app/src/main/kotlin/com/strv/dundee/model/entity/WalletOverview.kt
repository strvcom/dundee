package com.strv.dundee.model.entity

import java.text.NumberFormat
import java.util.*

data class WalletOverview(
		var coin: String,
		var amount: Double = 0.0,
		var boughtPrice: Double = 0.0
) {
	enum class ProfitState { PROFIT, LOSS, NONE }

	fun calculateAndFormatDifference(currency: String?, actualValue: Double?): String {
		if (actualValue == null || currency == null) return ""
		val difference = actualValue - boughtPrice
		val locale = when (currency) {
			Currency.USD -> Locale.US
			Currency.EUR -> Locale.GERMANY
			else -> Locale.US
		}
		return NumberFormat.getCurrencyInstance(locale).format(difference)
	}

	fun getProfitState(currency: String?, actualValue: Double?): ProfitState {
		if (actualValue == null || currency == null) return ProfitState.NONE
		return if(actualValue == boughtPrice) ProfitState.NONE else if (actualValue - boughtPrice > 0) ProfitState.PROFIT else ProfitState.LOSS
	}
}
