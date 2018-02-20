package com.strv.dundee.model.api.exchangerate

import android.arch.lifecycle.LiveData
import com.strv.ktools.Resource
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApiInterface {

	@GET("latest")
	fun getExchangeRate(@Query("base") source: String, @Query("symbols") target: String): LiveData<Resource<ExchangeRateResponse>>
}
