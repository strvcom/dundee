package com.strv.dundee.model.api.exchangerate

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApiInterface {

	@GET("latest")
	fun getExchangeRate(@Query("base") source: String, @Query("symbols") target: String): Call<ExchangeRateResponse>
}
