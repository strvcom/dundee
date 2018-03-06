package com.strv.dundee.model.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class WalletOverview(
	var coin: String,
	var amount: Double = 0.0,
	var firstWalletBoughtDate: Date? = null,
	val boughtPrices: MutableList<Pair<String, Double>> = mutableListOf()
) : Parcelable {

	fun getBoughtPrice(currency: String, exchangeRates: ExchangeRates?): Double =
		boughtPrices.sumByDouble {
			exchangeRates?.calculate(it.first, currency, it.second)
				?: 0.0
		}

	fun getProfit(currency: String, exchangeRates: ExchangeRates?, ticker: Ticker?): Double =
		(exchangeRates?.calculate(ticker?.currency, currency, ticker?.getValue(amount))
			?: 0.0) - getBoughtPrice(currency, exchangeRates)

	fun updateFirstWalletBoughtDate(boughtDate: Date?) {
		if (boughtDate != null && firstWalletBoughtDate != null && boughtDate.time < firstWalletBoughtDate!!.time) {
			firstWalletBoughtDate = boughtDate
		} else if (boughtDate != null) {
			firstWalletBoughtDate = boughtDate
		}
	}
}
