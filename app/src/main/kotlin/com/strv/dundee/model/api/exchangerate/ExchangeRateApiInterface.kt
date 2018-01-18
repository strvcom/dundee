package com.strv.dundee.model.api.exchangerate

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ExchangeRateApiInterface {

	@GET("latest")
	fun getEchangeRate(@Query("base") from: String, @Query("symbols") to: String): Call<ExchangeResponse>
}
