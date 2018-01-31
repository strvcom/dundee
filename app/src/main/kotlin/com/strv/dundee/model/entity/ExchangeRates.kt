package com.strv.dundee.model.entity

import android.arch.persistence.room.Entity
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
	var date: Date = Date(),
	var rates: Map<String, Double> = HashMap()
) {
	fun calculate(sourceCurrency: String, targetCurrency: String, amount: Double): Double = amount * getRate(sourceCurrency, targetCurrency)

	private fun getRate(sourceCurrency: String, targetCurrency: String): Double = rates[sourceCurrency]?.let { (rates[targetCurrency] ?: 0.0) / it } ?: 0.0
}