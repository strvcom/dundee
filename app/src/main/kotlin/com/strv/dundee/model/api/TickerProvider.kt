package com.strv.dundee.model.api

import com.strv.dundee.model.entity.Ticker


interface TickerProvider {
	fun getTicker(source: String, currency: String, coin: String): Ticker
}