package com.strv.dundee.model.entity

data class WalletOverview(
		var coin: String,
		var amount: Double = 0.0,
		var boughtPrice: Double = 0.0
) {
	enum class ProfitState { PROFIT, LOSS, NONE }

	fun getProfit(currency: String?, rate: ExchangeRate?, actualValue: Double?): Double {
		if (actualValue == null || currency == null) return 0.toDouble()
		return when(currency) {
			Currency.USD -> actualValue - boughtPrice
			rate?.target -> actualValue - boughtPrice * rate.rate
			else -> 0.toDouble()
		}
	}
}
