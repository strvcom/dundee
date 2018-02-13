package com.strv.dundee.model.api

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.entity.History
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.RetrofitResponse

interface BitcoinApi {
	fun getTicker(coin: String, currency: String): LiveData<RetrofitResponse<Ticker>>
	fun getHistory(coin:String, currency: String): LiveData<RetrofitResponse<History>>
}