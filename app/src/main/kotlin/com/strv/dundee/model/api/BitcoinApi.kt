package com.strv.dundee.model.api

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.entity.CandleSet
import com.strv.dundee.model.entity.Ticker
import retrofit2.Response

interface BitcoinApi {
	fun getTicker(coin: String, currency: String): LiveData<Response<Ticker>>
	fun getCandles(coin:String, currency: String, timeFrame: String): LiveData<Response<CandleSet>>
}