package com.strv.dundee.model.api

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.Resource

interface BitcoinApi {
	fun getTicker(coin: String, currency: String): LiveData<Resource<Ticker>>
}