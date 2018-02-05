package com.strv.dundee.model.api.coincap

import com.strv.dundee.model.api.bitfinex.BitfinexCandlesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CoincapApiInterface {

	@GET("page/{coin}")
	fun getTicker(@Path("coin") coin: String): Call<CoincapTickerResponse>

	@GET("history/{coin}")
	fun getHistory(@Path("coin") coin: String): Call<BitfinexCandlesResponse>
}
