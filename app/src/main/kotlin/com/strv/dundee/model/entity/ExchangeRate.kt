package com.strv.dundee.model.entity

import android.arch.persistence.room.Entity

/*
ExchangeRate object

Notes:
- data class needs to have default value so that it has empty constructor ready for Room
- properties also need to be vars because Room needs setters
 */
@Entity(tableName = "exchangeRate", primaryKeys = arrayOf("source", "target"))
data class ExchangeRate(
		var source: String = Currency.USD,
		var target: String = Currency.EUR,
		var rate: Double =  1.toDouble()
)