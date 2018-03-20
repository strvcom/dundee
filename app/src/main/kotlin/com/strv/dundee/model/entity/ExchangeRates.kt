package com.strv.dundee.model.entity

import android.arch.persistence.room.Entity
import android.os.Parcel
import com.strv.ktools.KParcelable
import com.strv.ktools.parcelableCreator
import com.strv.ktools.readDate
import com.strv.ktools.writeDate
import java.util.Date

/*
ExchangeRate object

Notes:
- data class needs to have default value so that it has empty constructor ready for Room
- properties also need to be vars because Room needs setters
 */
@Entity(tableName = "exchangeRates", primaryKeys = ["source"])
data class ExchangeRates(
	var source: String = Currency.USD,
	var date: Date? = Date(),
	var rates: Map<String, Double> = HashMap()
) : KParcelable {
	fun calculate(sourceCurrency: String?, targetCurrency: String?, amount: Double?): Double = amount?.times(getRate(sourceCurrency, targetCurrency)) ?: 0.0

	private fun getRate(sourceCurrency: String?, targetCurrency: String?): Double = rates[sourceCurrency]?.let { (rates[targetCurrency] ?: 0.0) / it } ?: 0.0

	private constructor(parcel: Parcel) : this(
		source = parcel.readString(),
		date = parcel.readDate()){
		parcel.readMap(rates, null)
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(source)
		parcel.writeDate(date)
		parcel.writeMap(rates)
	}

	companion object {
		@JvmField
		val CREATOR = parcelableCreator(::ExchangeRates)
	}
}