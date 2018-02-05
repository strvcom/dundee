package com.strv.dundee.model.api

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.entity.History
import com.strv.dundee.model.entity.Ticker
import retrofit2.Response

interface BitcoinApi {
	fun getTicker(coin: String, currency: String): LiveData<Response<Ticker>>
	fun getHistory(coin:String, currency: String): LiveData<Response<History>>
}