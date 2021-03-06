package com.strv.dundee.model.entity

import android.arch.persistence.room.Entity
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

/*
ExchangeRate object

Notes:
- data class needs to have default value so that it has empty constructor ready for Room
- properties also need to be vars because Room needs setters
 */
@Parcelize
@Entity(tableName = "exchangeRates", primaryKeys = ["source"])
data class ExchangeRates(
	var source: String = Currency.USD,
	var date: Date = Date(),
	var rates: Map<String, Double> = HashMap()
) : Parcelable {
	fun calculate(sourceCurrency: String?, targetCurrency: String?, amount: Double?): Double = amount?.times(getRate(sourceCurrency, targetCurrency)) ?: 0.0

	private fun getRate(sourceCurrency: String?, targetCurrency: String?): Double = rates[sourceCurrency]?.let { (rates[targetCurrency] ?: 0.0) / it } ?: 0.0
}