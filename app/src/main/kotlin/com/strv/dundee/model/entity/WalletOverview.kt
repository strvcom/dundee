package com.strv.dundee.model.entity

data class WalletOverview(
		var coin: String,
		var amount: Double = 0.0,
		var boughtPrice: Double = 0.0
) {
	fun getProfit(currency: String?, exchangeRate: ExchangeRate?, usdRate: ExchangeRate?, ticker: Ticker?): Double =
		if(ticker?.currency == exchangeRate?.source && currency == exchangeRate?.target && currency == usdRate?.target)
			ticker!!.lastPrice * amount * exchangeRate!!.rate - boughtPrice * usdRate!!.rate
		else 0.0
}
