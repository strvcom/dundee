package com.strv.dundee.model.entity

data class HistroyPrice(
	val timestamp: Long = 0,
	val open: Double = 0.0,
	val close: Double = 0.0,
	val high: Double = 0.0,
	val low: Double = 0.0,
	val volume: Double = 0.0
) {
	val middle
		get() = (high + low) / 2
}