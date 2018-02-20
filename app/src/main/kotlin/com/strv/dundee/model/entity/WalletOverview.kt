package com.strv.dundee.model.entity

import android.os.Parcel
import com.strv.ktools.KParcelable
import com.strv.ktools.parcelableCreator
import com.strv.ktools.readDate
import com.strv.ktools.writeDate
import java.util.Date

data class WalletOverview(
	var coin: String,
	var amount: Double = 0.0,
	var firstWalletBoughtDate: Date? = null,
	val boughtPrices: MutableList<Pair<String, Double>> = mutableListOf()
) : KParcelable {

	fun getBoughtPrice(currency: String, exchangeRates: ExchangeRates?): Double =
		boughtPrices.sumByDouble {
			exchangeRates?.calculate(it.first, currency, it.second)
				?: 0.0
		}

	fun getProfit(currency: String, exchangeRates: ExchangeRates?, ticker: Ticker?): Double =
		(exchangeRates?.calculate(ticker?.currency, currency, ticker?.getValue(amount))
			?: 0.0) - getBoughtPrice(currency, exchangeRates)

	private constructor(parcel: Parcel) : this(
		coin = parcel.readString(),
		amount = (parcel.readValue(Double::class.java.classLoader) as? Double)!!,
		firstWalletBoughtDate = parcel.readDate()) {
		parcel.readList(boughtPrices, null)
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(coin)
		parcel.writeValue(amount)
		parcel.writeDate(firstWalletBoughtDate)
		parcel.writeList(boughtPrices)
	}

	companion object {
		@JvmField
		val CREATOR = parcelableCreator(::WalletOverview)
	}

	fun updateFirstWalletBoughtDate(boughtDate: Date?) {
		if (boughtDate != null && firstWalletBoughtDate != null && boughtDate.time < firstWalletBoughtDate!!.time) {
			firstWalletBoughtDate = boughtDate
		} else if (boughtDate != null) {
			firstWalletBoughtDate = boughtDate
		}
	}
}
