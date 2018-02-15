package com.strv.dundee.model.api.coincap

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CoincapApiInterface {

	@GET("history/{coin}")
	fun getHistory(@Path("coin") coin: String): Call<CoincapHistoryResponse>
}
