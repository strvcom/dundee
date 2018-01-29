package com.strv.dundee.model.api

import retrofit2.Call

interface BitcoinApi {
	fun getTicker(coin: String, currency: String): Call<out TickerProvider>
	fun getCandles(coin:String, currency: String, timeFrame: String): Call<out CandlesProvider>
}