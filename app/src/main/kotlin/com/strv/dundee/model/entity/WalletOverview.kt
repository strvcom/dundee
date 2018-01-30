package com.strv.dundee.model.entity

import android.os.Parcel
import com.strv.dundee.model.repo.ExchangeRatesLiveData
import com.strv.ktools.KParcelable
import com.strv.ktools.parcelableCreator

data class WalletOverview(
	var coin: String,
	var amount: Double = 0.0,
	val boughtPrices: MutableList<Pair<String, Double>> = mutableListOf()
) : KParcelable {

	fun getBoughtPrice(currency: String?, exchangeRates: HashMap<String, ExchangeRatesLiveData>): Double =
		boughtPrices.sumByDouble { (exchangeRates[it.first]?.value?.data?.rates!![currency] ?: 0.0) * it.second }

	fun getProfit(currency: String, exchangeRates: HashMap<String, ExchangeRatesLiveData>, ticker: Ticker): Double =
		ticker.getValue(amount, currency, exchangeRates) - getBoughtPrice(currency, exchangeRates)

	private constructor(parcel: Parcel) : this(
		coin = parcel.readString(),
		amount = (parcel.readValue(Double::class.java.classLoader) as? Double)!!) {
		parcel.readList(boughtPrices, null)
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(coin)
		parcel.writeValue(amount)
		parcel.writeList(boughtPrices)
	}

	companion object {
		@JvmField
		val CREATOR = parcelableCreator(::WalletOverview)
	}
}
