package com.strv.dundee.model.api

import com.strv.dundee.model.entity.CandleSet

interface CandlesProvider {
	fun getCandles(source: String, currency: String, coin: String, timeFrame: String): CandleSet
}