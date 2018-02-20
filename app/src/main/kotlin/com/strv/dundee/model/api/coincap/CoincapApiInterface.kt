package com.strv.dundee.model.api.coincap

import android.arch.lifecycle.LiveData
import com.strv.ktools.Resource
import retrofit2.http.GET
import retrofit2.http.Path

interface CoincapApiInterface {

	@GET("history/{coin}")
	fun getHistory(@Path("coin") coin: String): LiveData<Resource<CoincapHistoryResponse>>

	@GET("history/{timeFrame}/{coin}")
	fun getHistory(@Path("coin") coin: String, @Path("timeFrame") timeFrame: String): LiveData<Resource<CoincapHistoryResponse>>
}
