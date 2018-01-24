package com.strv.dundee.model.entity

import android.arch.persistence.room.Entity

/*
ExchangeRate object

Notes:
- data class needs to have default value so that it has empty constructor ready for Room
- properties also need to be vars because Room needs setters
 */
@Entity(tableName = "exchangeRates", primaryKeys = arrayOf("source"))
data class ExchangeRates(
		var source: String = Currency.USD,
		var rates: Map<String, Double> = HashMap()
)